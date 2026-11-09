package minisocs;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import layer.rest.RestServer;
import layer.sse.SseAdapter;
import minisocs.api.constants.NewMessageNotificationStrategy;
import minisocs.api.port.driving.forrequestingminisocs.commands.AddMemberToSocialNetworkCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.AddUserCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.CreateSocialNetworkCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.PostMessageCommand;
import minisocs.common.UtilSerialiser;

class TestSseNotifications {

    private static final String mod = "moderator";
    private static final String member = "member";
    private static final String networkname = "best network";
    private static final String uri_for_rest = "http://localhost:8083/MiniSocs";
    private static final String usi_for_sse = "http://localhost:8084/MySseServer";

    private SseAdapter sse_adap;
    private RestServer restserver;
    private Client client;
    private WebTarget rest;
    private WebTarget sse;
    private final List<SseNotificationConsumer> list_of_consumers = new ArrayList<>();

    @BeforeEach
    void setUp() throws Exception {
        sse_adap = new SseAdapter(usi_for_sse);
        sse_adap.startSseServer();
        var facade = new MiniSocs(sse_adap);
        restserver = new RestServer(uri_for_rest, facade);
        restserver.startRestServer();
        client = ClientBuilder.newClient();
        rest = client.target(uri_for_rest);
        sse = client.target(usi_for_sse);
        waitUntilReady(rest, "isServerReady");
        waitUntilReady(sse, "isready");
        check_if_works(post(rest, "/user/add", user(mod)));
        check_if_works(post(rest, "/socialnetwork/create", new CreateSocialNetworkCommand(mod, networkname)));
        check_if_works(post(rest, "/user/add", user(member)));
        check_if_works(put(rest, "/socialnetwork/addMember", new AddMemberToSocialNetworkCommand(mod, networkname, member)));
        check_if_works(sse.path("/notification/create").request().post(Entity.text(networkname)));
    }

    @AfterEach
    void tearDown() {
        list_of_consumers.forEach(SseNotificationConsumer::close);
        list_of_consumers.clear();
        client.close();
        restserver.shutRestServerDown();
        sse_adap.shutSseServerDown();
    }

    @Test
    void message_by_sse() throws Exception {
        var message = new AtomicInteger();
        connect("member", sse.path("/notification/register")
                .queryParam("snname", networkname).queryParam("strategy", "IMMEDIATE"), message);
        check_if_works(put(rest, "/participation/post",
                new PostMessageCommand(mod, networkname, "message", Instant.now())));

        Awaitility.await().atMost(Duration.ofSeconds(5)).until(() -> message.get() >= 1);
    }

    @Test
    void no_post_no_notif() throws Exception {
        var received = new AtomicInteger();
        connect("member", sse.path("/notification/register")
                .queryParam("snname", networkname).queryParam("strategy", "IMMEDIATE"), received);

        Thread.sleep(1000);
        Assertions.assertEquals(0, received.get());
    }

    @Test
    void sse_mod_request_for_mod() throws Exception {
        var received = new AtomicInteger();
        connect("moderator", sse.path("/notification/registermoderator").queryParam("snname", networkname), received);

        check_if_works(put(rest, "/participation/post",
                new PostMessageCommand(member, networkname, "mod req", Instant.now())));

        Awaitility.await().atMost(Duration.ofSeconds(5)).until(() -> received.get() >= 1);
    }


    private void connect(final String label, final WebTarget endpoint, final AtomicInteger counter) throws Exception {
        var consumer = new SseNotificationConsumer(label, endpoint, notification -> {
            System.out.println("[" + label + "] notification: " + notification);
            counter.incrementAndGet();
        }, problem -> System.out.println("[" + label + "] error: " + problem), () -> { });

        list_of_consumers.add(consumer);
        Awaitility.await().atMost(Duration.ofSeconds(5)).until(consumer::isOpen);
        Thread.sleep(300);
    }

    private static void waitUntilReady(final WebTarget target, final String answer) {
        Awaitility.await().atMost(Duration.ofSeconds(5)).until(() -> {
            try {
                var response = target.path("/isready").request().get();
                return response.getStatus() == 200 && answer.equals(response.readEntity(String.class));
            } catch (Exception problem) {
                return false;
            }
        });
    }

    private static AddUserCommand user(final String pseudo) {
        return new AddUserCommand(pseudo, "sargsyan" + pseudo, "mikayel" + pseudo, pseudo + "@gmail.com",
                NewMessageNotificationStrategy.IMMEDIATE);
    }

    private static Response post(final WebTarget target, final String path, final Object command) throws Exception {
        return target.path(path).request().post(Entity.text(UtilSerialiser.writeValueAsString(command)));
    }

    private static Response put(final WebTarget target, final String path, final Object command) throws Exception {
        return target.path(path).request().put(Entity.text(UtilSerialiser.writeValueAsString(command)));
    }

    private static void check_if_works(final Response response) {
        response.bufferEntity();
        Assertions.assertEquals(200, response.getStatus(), () -> "server said: " + response.readEntity(String.class));
    }
}
