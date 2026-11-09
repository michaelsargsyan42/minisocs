package anomalydetection.adapter.amqp;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;
import anomalydetection.api.port.driven.foralerting.ForAlerting;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import anomalydetection.api.constants.AMQPNames;
import anomalydetection.api.port.driven.foralerting.events.AnomalyDetectionEvent;
import anomalydetection.common.Log;
import anomalydetection.common.UtilSerialiser;


public class ProduceIncidentAdapter implements ForAlerting, AutoCloseable {
    private final Connection conn;
    private final Channel chnl;


    private static final String exchangenamestring = AMQPNames.AMQP_EXCHANGE_NAME.toString();
    private static final String key2 = AMQPNames.ANOMALY_DETECTION + "." + AMQPNames.ADMINISTRATOR + "."
            + AnomalyDetectionEvent.class.getSimpleName();

    @Override
    public void close() throws IOException, TimeoutException {
        chnl.close();
        conn.close();
    }
    public ProduceIncidentAdapter(final String uri) throws IOException, TimeoutException, URISyntaxException,
            NoSuchAlgorithmException, KeyManagementException {

        var connectionfactory = new ConnectionFactory();
        connectionfactory.setUri(uri);

        conn = connectionfactory.newConnection();
        chnl = conn.createChannel();

        chnl.exchangeDeclare(exchangenamestring, BuiltinExchangeType.TOPIC);
    }

    @Override
    public void alert(final AnomalyDetectionEvent event) {
        try {
            var eventjson = UtilSerialiser.writeValueAsString(event);
            chnl.basicPublish(exchangenamestring, key2, null, eventjson.getBytes(StandardCharsets.UTF_8));
            Log.AMQP.info("incid published {}: with key {}", key2, eventjson);
        } catch (Exception problem) {
            Log.AMQP.error("problem publishing incident {}", problem.getMessage());
        }
    }


}