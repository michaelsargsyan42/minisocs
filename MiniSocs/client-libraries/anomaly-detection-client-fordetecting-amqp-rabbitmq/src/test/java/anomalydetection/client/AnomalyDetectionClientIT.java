package anomalydetection.client;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.ConnectionFactory;

import anomalydetection.api.constants.AMQPNames;
import anomalydetection.api.constants.AnomalyType;
import anomalydetection.api.port.driving.fordetecting.api.DetectionTarget;
import anomalydetection.api.port.driving.fordetecting.events.AddAnomalyDetectionEvent;
import anomalydetection.api.port.driving.fordetecting.events.AddNewMessageAndDetectAnomalyEvent;
import anomalydetection.api.port.driving.fordetecting.events.Message;

// client lib int tests

class AnomalyDetectionClientIT {
    private static final String BROKER_URI = "amqp://localhost:5672";

    @Test
    void inappropriateMessageRaisesAnAlert() throws Exception {
        var alert = new CountDownLatch(1);
        var factory = new ConnectionFactory();
        factory.setUri(BROKER_URI);
        try (var connection = factory.newConnection(); var channel = connection.createChannel();
                var client = new AnomalyDetectionClient(BROKER_URI)) {

            // admin gets alerts
            var exchange = AMQPNames.AMQP_EXCHANGE_NAME.toString();
            channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC);
            var queue = channel.queueDeclare().getQueue();
            channel.queueBind(queue, exchange, AMQPNames.ANOMALY_DETECTION + "." + AMQPNames.ADMINISTRATOR + ".#");
            channel.basicConsume(queue, true, (tag, delivery) -> alert.countDown(), tag -> { });

            client.addAnAnomalyDetectionType(new AddAnomalyDetectionEvent(new DetectionTarget("csc5002", "member"),
                    AnomalyType.INAPPROPRIATE_VOCABULARY));
            client.addNewMessageAndPerformAnomalyDetection(new AddNewMessageAndDetectAnomalyEvent(
                    new Message("csc5002", "member", Instant.now(), "unsuitable")));

            Assertions.assertTrue(alert.await(10, TimeUnit.SECONDS), "admin got no alerts");
        }
    }
}
