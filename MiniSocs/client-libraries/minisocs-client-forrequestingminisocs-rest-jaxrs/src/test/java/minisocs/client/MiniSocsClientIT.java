package minisocs.client;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import minisocs.api.constants.NewMessageNotificationStrategy;
import minisocs.api.exceptions.UnfeasibleOperation;

// minisocs cl int tests

class MiniSocsClientIT {
    private static final String NETWORK = "csc5002";
    private static MiniSocsClient client;

    @BeforeAll
    static void setUp() throws Exception {
        client = new MiniSocsClient("http://localhost:8083/MiniSocs", "http://localhost:8084/MySseServer");
        addUser("moderator");
        addUser("member");
        addUser("nonMember");
        client.createSocialNetwork("moderator", NETWORK);
        client.addMember("moderator", NETWORK, "member");
    }

    @AfterAll
    static void tearDown() {
        client.close();
    }

    @Test
    void restMethods() throws Exception {
        Assertions.assertTrue(client.listUsers().contains("\"nonMember\""));
        Assertions.assertTrue(client.listSocialNetworks().contains("\"" + NETWORK + "\""));
        Assertions.assertTrue(client.listMembers(NETWORK).contains("\"member\""));
        Assertions.assertTrue(client.listModerators(NETWORK).contains("\"moderator\""));
        var instant = client.postMessage("member", NETWORK, "hello");
        client.moderateMessage("moderator", NETWORK, instant, true);
        client.readLatestMessages("member", NETWORK);
        Assertions.assertThrows(UnfeasibleOperation.class, () -> client.postMessage("nonMember", NETWORK, "hello"));
        client.deactivateUserAccount("nonMember");
    }

    @Test
    void memberReceivesNewMessages() throws Exception {
        var received = new AtomicInteger();
        try (var consumer = client.receiveNewMessages(NETWORK, NewMessageNotificationStrategy.IMMEDIATE,
                notification -> received.incrementAndGet())) {
            Awaitility.await().atMost(Duration.ofSeconds(5)).until(consumer::isOpen);
            Thread.sleep(300);
            client.postMessage("moderator", NETWORK, "bonjour world ! mike here");
            Awaitility.await().atMost(Duration.ofSeconds(5)).until(() -> received.get() >= 1);
        }
    }

    @Test
    void moderatorReceivesModerationRequests() throws Exception {
        var received = new AtomicInteger();
        try (var consumer = client.receiveModerationRequests(NETWORK, notification -> received.incrementAndGet())) {
            Awaitility.await().atMost(Duration.ofSeconds(5)).until(consumer::isOpen);
            Thread.sleep(300);
            client.postMessage("member", NETWORK, "moderate moderate moderate !!!!!");
            Awaitility.await().atMost(Duration.ofSeconds(5)).until(() -> received.get() >= 1);
        }
    }

    private static void addUser(final String pseudo) throws Exception {
        client.addUser(pseudo, "name" + pseudo, "firstName" + pseudo, pseudo + "@mail.am",
                NewMessageNotificationStrategy.IMMEDIATE);
    }
}
