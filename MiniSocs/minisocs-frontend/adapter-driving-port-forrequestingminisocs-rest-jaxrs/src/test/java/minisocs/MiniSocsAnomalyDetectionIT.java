package minisocs;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import layer.amqp.ForDetectingAmqpAdapter;
import layer.rest.RestServer;
import minisocs.api.constants.AMQPNames;
import minisocs.api.constants.NewMessageNotificationStrategy;
import minisocs.api.port.driven.fordetecting.api.command.AnomalyType;
import minisocs.api.port.driven.fordetecting.api.command.DetectionTarget;
import minisocs.api.port.driving.forrequestingminisocs.commands.AddAnomalyDetectionCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.AddUserCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.CreateSocialNetworkCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.PostMessageCommand;
import minisocs.common.UtilSerialiser;

class MiniSocsAnomalyDetectionIT {
    private static final String rest_uri = "http://localhost:8083/MiniSocs";
    private static final String ready_answer_server = "isServerReady";
    private static final String broker_uri = "amqp://localhost:5672";
    private static final String amqp_exchange_name = AMQPNames.AMQP_EXCHANGE_NAME.toString();
    private static final String ADMIN_BINDING = AMQPNames.ANOMALY_DETECTION + "." + AMQPNames.ADMINISTRATOR + ".#";

    private static final String mod_name = "moderator";
    private static final String network_name = "csc5002";

    private ForDetectingAmqpAdapter detectingAdapter;
    private RestServer restServer;
    private Client client;
    private WebTarget rest;

    private Connection connection;
    private Channel channel;
    private CountDownLatch incidentReceived;

    @BeforeEach
    void setUp() throws Exception {
        detectingAdapter = new ForDetectingAmqpAdapter(broker_uri);
        var facade = new MiniSocs(null, detectingAdapter);
        restServer = new RestServer(rest_uri, facade);
        restServer.startRestServer();
        client = ClientBuilder.newClient();
        rest = client.target(rest_uri);
        Awaitility.await().atMost(Duration.ofSeconds(5)).until(() -> {
            try {
                var response = rest.path("/isready").request().get();
                return response.getStatus() == 200 && ready_answer_server.equals(response.readEntity(String.class));
            } catch (Exception problem) {
                return false;
            }
        });

        incidentReceived = new CountDownLatch(1);
        var factory = new ConnectionFactory();
        factory.setUri(broker_uri);
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(amqp_exchange_name, BuiltinExchangeType.TOPIC);
        var adminQueue = channel.queueDeclare().getQueue();
        channel.queueBind(adminQueue, amqp_exchange_name, ADMIN_BINDING);
        channel.basicConsume(adminQueue, true, (tag, delivery) -> {
            System.out.println("[administrator] incident: " + new String(delivery.getBody(), StandardCharsets.UTF_8));
            incidentReceived.countDown();
        }, tag -> { });
    }

    @AfterEach
    void tearDown() throws Exception {
        if (channel != null) {
            channel.close();
        }
        if (connection != null) {
            connection.close();
        }
        if (client != null) {
            client.close();
        }
        if (restServer != null) {
            restServer.shutRestServerDown();
        }
        if (detectingAdapter != null) {
            detectingAdapter.close();
        }
    }

    @Test
    void inappropriateMessageReachesTheAdministrator() throws Exception {
        createModeratorAndNetwork();
        askForDetection();
        Thread.sleep(500);

        check_if_works_else_error(put("/participation/post", new PostMessageCommand(mod_name, network_name, "unsuitable", Instant.now())));

        Assertions.assertTrue(incidentReceived.await(10, TimeUnit.SECONDS), "the administrator received no incident");
    }

    @Test
    void harmlessMessageRaisesNoIncident() throws Exception {
        createModeratorAndNetwork();
        askForDetection();
        Thread.sleep(500);

        check_if_works_else_error(put("/participation/post",
                new PostMessageCommand(mod_name, network_name, "hello everyone", Instant.now())));

        Assertions.assertFalse(incidentReceived.await(3, TimeUnit.SECONDS),
                "an incident was received for a harmless message");
    }


    private void createModeratorAndNetwork() throws Exception {
        check_if_works_else_error(post("/user/add", new AddUserCommand(mod_name, "nameModerator", "firstNameModerator",
                "moderator@courriel.fr", NewMessageNotificationStrategy.IMMEDIATE)));
        check_if_works_else_error(post("/socialnetwork/create", new CreateSocialNetworkCommand(mod_name, network_name)));
    }

    private void askForDetection() throws Exception {
        check_if_works_else_error(post("/anomalydetection/add", new AddAnomalyDetectionCommand(new DetectionTarget(network_name, mod_name),
                AnomalyType.INAPPROPRIATE_VOCABULARY)));
    }

    private Response post(final String path, final Object command) throws Exception {
        return rest.path(path).request().post(Entity.text(UtilSerialiser.writeValueAsString(command)));
    }

    private Response put(final String path, final Object command) throws Exception {
        return rest.path(path).request().put(Entity.text(UtilSerialiser.writeValueAsString(command)));
    }

    private static void check_if_works_else_error(final Response response) {
        response.bufferEntity();
        Assertions.assertEquals(200, response.getStatus(), () -> "server said: " + response.readEntity(String.class));
    }
}
