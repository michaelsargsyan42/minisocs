package minisocs.systemtest;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import minisocs.api.constants.NewMessageNotificationStrategy;
import minisocs.api.exceptions.UnfeasibleOperation;
import minisocs.api.port.driven.fordetecting.api.command.AnomalyType;
import minisocs.client.MiniSocsClient;

class MiniSocsSystemIT {
    private static final String network_name = "csc5002";

    @Test
    void scenario() throws Exception {
        try (var client = new MiniSocsClient("http://localhost:8083/MiniSocs", "http://localhost:8084/MySseServer")) {
            for (var pseudo : new String[] {"moderator", "member", "nonMember"}) {
                client.addUser(pseudo, "name" + pseudo, "firstName" + pseudo, pseudo + "@courriel.fr",
                        NewMessageNotificationStrategy.IMMEDIATE);
            }
            client.createSocialNetwork("moderator", network_name);
            client.addMember("moderator", network_name, "member");
            Assertions.assertTrue(client.listMembers(network_name).contains("\"member\""));
            client.addAnAnomalyDetectionType(network_name, "member", AnomalyType.INAPPROPRIATE_VOCABULARY);

            var newMessages = new AtomicInteger();
            var moderationRequests = new AtomicInteger();
            try (var memberStream = client.receiveNewMessages(network_name, NewMessageNotificationStrategy.IMMEDIATE,
                    notification -> newMessages.incrementAndGet());
                    var moderatorStream = client.receiveModerationRequests(network_name,
                            notification -> moderationRequests.incrementAndGet())) {
                Awaitility.await().atMost(Duration.ofSeconds(5))
                        .until(() -> memberStream.isOpen() && moderatorStream.isOpen());
                Thread.sleep(300);

                // mod message accept and member is notified
                client.postMessage("moderator", network_name, "mod says bonjour");
                Awaitility.await().atMost(Duration.ofSeconds(5)).until(() -> newMessages.get() >= 1);

                // member needs moderation, mod accepts
                var instant = client.postMessage("member", network_name, "member says what is bon about this jour");
                Awaitility.await().atMost(Duration.ofSeconds(5)).until(() -> moderationRequests.get() >= 1);
                client.moderateMessage("moderator", network_name, instant, true);
            }

            Assertions.assertThrows(UnfeasibleOperation.class,
                    () -> client.postMessage("nonMember", network_name, "hello ??"));
            client.closeSocialNetwork("moderator", network_name);
            Assertions.assertThrows(UnfeasibleOperation.class,
                    () -> client.postMessage("moderator", network_name, "too late :("));
        }
    }
}
