package anomalydetection.client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import anomalydetection.api.constants.AMQPNames;
import anomalydetection.api.port.driving.fordetecting.events.AddAnomalyDetectionEvent;
import anomalydetection.api.port.driving.fordetecting.events.AddNewMessageAndDetectAnomalyEvent;
import anomalydetection.common.UtilSerialiser;

public class AnomalyDetectionClient implements AutoCloseable {
    private static final String amqp_exchange_name = AMQPNames.AMQP_EXCHANGE_NAME.toString();
    private static final String ROUTING_KEY_PREFIX =
            AMQPNames.MINISOCS_FRONTEND + "." + AMQPNames.ANOMALY_DETECTION + ".";

    private final Connection conn;
    private final Channel chnnl;

    public AnomalyDetectionClient(final String brokerURI) throws IOException, TimeoutException,
            URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
        var factory = new ConnectionFactory();
        factory.setUri(brokerURI);
        conn = factory.newConnection();
        chnnl = conn.createChannel();
        chnnl.exchangeDeclare(amqp_exchange_name, BuiltinExchangeType.TOPIC);
    }

    public void addAnAnomalyDetectionType(final AddAnomalyDetectionEvent event) throws IOException {
        publish(event);
    }

    public void addNewMessageAndPerformAnomalyDetection(final AddNewMessageAndDetectAnomalyEvent event)
            throws IOException {
        publish(event);
    }

    private synchronized void publish(final Object event) throws IOException {
        var routingKey = ROUTING_KEY_PREFIX + event.getClass().getSimpleName();
        var json = UtilSerialiser.writeValueAsString(event);
        chnnl.basicPublish(amqp_exchange_name, routingKey, null, json.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void close() throws IOException, TimeoutException {
        chnnl.close();
        conn.close();
    }
}
