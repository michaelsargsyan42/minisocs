package anomalydetection.adapter.amqp;

import java.io.IOException;
import java.net.URISyntaxException;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Delivery;
import anomalydetection.api.constants.AMQPNames;
import anomalydetection.api.port.driving.fordetecting.api.AnomalyDetectionAPI;
import anomalydetection.api.port.driving.fordetecting.events.AddAnomalyDetectionEvent;
import anomalydetection.api.port.driving.fordetecting.events.AddNewMessageAndDetectAnomalyEvent;
import anomalydetection.common.Log;
import anomalydetection.common.UtilSerialiser;

public class ConsumeAnomalyDetectionAdapter implements AutoCloseable {

    private final Connection con;
    private final Channel chnl;
    private static final String exchangestringname = AMQPNames.AMQP_EXCHANGE_NAME.toString();
    private static final String key =
            AMQPNames.MINISOCS_FRONTEND+"."+AMQPNames.ANOMALY_DETECTION+".#";
    private final AnomalyDetectionAPI facade;
    
    

    public ConsumeAnomalyDetectionAdapter(final String uri, final AnomalyDetectionAPI facade)
            throws IOException, TimeoutException, URISyntaxException, NoSuchAlgorithmException,
            KeyManagementException {
        this.facade = facade;

        var connection_factory = new ConnectionFactory();
        connection_factory.setUri(uri);

        con = connection_factory.newConnection();
        chnl = con.createChannel();

        chnl.exchangeDeclare(exchangestringname, BuiltinExchangeType.TOPIC);
        var queueName = chnl.queueDeclare().getQueue();
        chnl.queueBind(queueName, exchangestringname, key);

        chnl.basicConsume(queueName, true, (consumerTag, delivery) -> dispatch(delivery),
                consumerTag -> { });

        Log.AMQP.info("on queue {} to key {}", queueName, key);
    }

    @Override
    public void close() throws IOException, TimeoutException {
        chnl.close();
        con.close();
    }
    private void dispatch(final Delivery delivery) {
        var key1 = delivery.getEnvelope().getRoutingKey();

        var body = new String(delivery.getBody());

        var event = key1.substring(key1.lastIndexOf('.')+1);
        Log.AMQP.info("received event {}, the content is {}", event, body);

        try {
            if (AddAnomalyDetectionEvent.class.getSimpleName().equals(event)) {
                facade.addAnAnomalyDetectionType(UtilSerialiser.readValue(body, AddAnomalyDetectionEvent.class));
            } else if (AddNewMessageAndDetectAnomalyEvent.class.getSimpleName().equals(event)) {
                facade.addNewMessageAndPerformAnomalyDetection(
                        UtilSerialiser.readValue(body, AddNewMessageAndDetectAnomalyEvent.class));
            } else {
                Log.AMQP.warn("ignored the unknown event with content of {}", event);
            }
        } catch (Exception problem) {
            Log.AMQP.error("handling the event problem {} with message {}", event, problem.getMessage());
        }
    }



}