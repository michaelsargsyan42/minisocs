package anomalydetection;

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

import anomalydetection.adapter.amqp.ConsumeAnomalyDetectionAdapter;
import anomalydetection.adapter.amqp.ProduceIncidentAdapter;
import anomalydetection.adapter.orm.OrmIncidentsAdapter;
import anomalydetection.api.constants.AMQPNames;
import anomalydetection.api.constants.AnomalyType;
import anomalydetection.api.constants.ORMEclipseLink;
import anomalydetection.api.port.driving.fordetecting.api.DetectionTarget;
import anomalydetection.api.port.driving.fordetecting.events.AddAnomalyDetectionEvent;
import anomalydetection.api.port.driving.fordetecting.events.AddNewMessageAndDetectAnomalyEvent;
import anomalydetection.api.port.driving.fordetecting.events.Message;
import anomalydetection.common.UtilSerialiser;

class AnomalyDetectionIT {
    private static final String broker = "amqp://localhost:5672";
    private static final String exchange_name = AMQPNames.AMQP_EXCHANGE_NAME.toString();
    private static final String KEY_PREFIX_FROM_FRONTEND =
            AMQPNames.MINISOCS_FRONTEND + "." + AMQPNames.ANOMALY_DETECTION + ".";
    private static final String ADMIN_BINDING =
            AMQPNames.ANOMALY_DETECTION + "." + AMQPNames.ADMINISTRATOR + ".#";

    private static final String pseudo = "member";
    private static final String networkname = "csc5002";

    private ProduceIncidentAdapter produceAdapter;
    private OrmIncidentsAdapter ormAdapter;
    private ConsumeAnomalyDetectionAdapter consumeAdapter;
    private Connection connection;
    private Channel channel;
    private CountDownLatch incidentReceived;

    @BeforeEach
    void setUp() throws Exception {
        produceAdapter = new ProduceIncidentAdapter(broker);
        ormAdapter = new OrmIncidentsAdapter("localhost", ORMEclipseLink.NETWORKSERVER_PORT,
                ORMEclipseLink.LOCAL_PATH, ORMEclipseLink.DERBY_CLIENT_DRIVER, ORMEclipseLink.PERSISTENCE_UNIT_NAME);
        ormAdapter.deleteAllIncidents();
        var facade = new AnomalyDetection(produceAdapter, ormAdapter);
        consumeAdapter = new ConsumeAnomalyDetectionAdapter(broker, facade);

        var factory = new ConnectionFactory();
        factory.setUri(broker);
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(exchange_name, BuiltinExchangeType.TOPIC);

        incidentReceived = new CountDownLatch(1);
        var adminQueue = channel.queueDeclare().getQueue();
        channel.queueBind(adminQueue, exchange_name, ADMIN_BINDING);
        channel.basicConsume(adminQueue, true, (tag, delivery) -> {
            System.out.println("[administrator] incident: "
                    + new String(delivery.getBody(), StandardCharsets.UTF_8));
            incidentReceived.countDown();
        }, tag -> { });
    }

    @AfterEach
    void tearDown() throws Exception {
        channel.close();
        connection.close();
        consumeAdapter.close();
        produceAdapter.close();
        ormAdapter.deleteAllIncidents();
        ormAdapter.close();
    }

    @Test
    void inappropriateMessageIsAlertedAndStored() throws Exception {
        askForDetection(AnomalyType.INAPPROPRIATE_VOCABULARY);
        Thread.sleep(300);

        postForAnalysis("unsuitable");

        Assertions.assertTrue(incidentReceived.await(5, TimeUnit.SECONDS),
                "no incident was published to the administrator");
        Awaitility.await().atMost(Duration.ofSeconds(5)).until(() -> ormAdapter.getIncidents().size() == 1);
        var stored = ormAdapter.getIncidents().get(0);
        Assertions.assertEquals(pseudo, stored.getPseudo());
        Assertions.assertEquals(networkname, stored.getSnname());
        Assertions.assertEquals(AnomalyType.INAPPROPRIATE_VOCABULARY.name(), stored.getType());
        System.out.println("[database] " + stored);
    }

    @Test
    void tooLongMessageIsAlertedAndStoredTruncated() throws Exception {
        askForDetection(AnomalyType.LONG_MESSAGE);
        Thread.sleep(300);

        postForAnalysis("word ".repeat(500));

        Assertions.assertTrue(incidentReceived.await(5, TimeUnit.SECONDS),
                "no incident was published for an over-long message");
        Awaitility.await().atMost(Duration.ofSeconds(5)).until(() -> ormAdapter.getIncidents().size() == 1);
        var stored = ormAdapter.getIncidents().get(0);
        Assertions.assertEquals(AnomalyType.LONG_MESSAGE.name(), stored.getType());
        Assertions.assertTrue(stored.getContent().length() <= OrmIncidentsAdapter.maximumlength);
    }

    @Test
    void harmlessMessageRaisesNoIncident() throws Exception {
        askForDetection(AnomalyType.INAPPROPRIATE_VOCABULARY);
        Thread.sleep(300);

        postForAnalysis("hello everyone");

        Assertions.assertFalse(incidentReceived.await(2, TimeUnit.SECONDS),
                "an incident was published for a harmless message");
        Assertions.assertTrue(ormAdapter.getIncidents().isEmpty(), "an incident was stored for a harmless message");
    }

    @Test
    void messageWithNoDetectorRaisesNoIncident() throws Exception {
        postForAnalysis("unsuitable");

        Assertions.assertFalse(incidentReceived.await(2, TimeUnit.SECONDS),
                "an incident was published for a user nobody is watching");
        Assertions.assertTrue(ormAdapter.getIncidents().isEmpty());
    }


    private void askForDetection(final AnomalyType type) throws Exception {
        publish(new AddAnomalyDetectionEvent(new DetectionTarget(networkname, pseudo), type));
    }

    private void postForAnalysis(final String content) throws Exception {
        publish(new AddNewMessageAndDetectAnomalyEvent(new Message(networkname, pseudo, Instant.now(), content)));
    }

    private void publish(final Object event) throws Exception {
        var routingKey = KEY_PREFIX_FROM_FRONTEND + event.getClass().getSimpleName();
        var json = UtilSerialiser.writeValueAsString(event);
        channel.basicPublish(exchange_name, routingKey, null, json.getBytes(StandardCharsets.UTF_8));
        System.out.println("[frontend] published " + routingKey + ": " + json);
    }
}