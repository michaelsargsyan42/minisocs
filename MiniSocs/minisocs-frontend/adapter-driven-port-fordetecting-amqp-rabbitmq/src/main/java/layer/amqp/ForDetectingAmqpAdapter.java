package layer.amqp;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import anomalydetection.api.constants.AnomalyType;
import anomalydetection.api.port.driving.fordetecting.api.DetectionTarget;
import anomalydetection.api.port.driving.fordetecting.events.AddAnomalyDetectionEvent;
import anomalydetection.api.port.driving.fordetecting.events.AddNewMessageAndDetectAnomalyEvent;
import anomalydetection.api.port.driving.fordetecting.events.Message;
import anomalydetection.client.AnomalyDetectionClient;
import minisocs.api.exceptions.UnfeasibleOperation;
import minisocs.api.port.driven.fordetecting.api.ForDetecting;
import minisocs.api.port.driven.fordetecting.api.command.AddNewMessageAndDetectAnomalyCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.AddAnomalyDetectionCommand;


public class ForDetectingAmqpAdapter implements ForDetecting, AutoCloseable {
    private final AnomalyDetectionClient client_anomaly_detection;

    public ForDetectingAmqpAdapter(final String brokerURI) throws IOException, TimeoutException,
            URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
        client_anomaly_detection = new AnomalyDetectionClient(brokerURI);
    }

    @Override
    public void addAnAnomalyDetectionType(final AddAnomalyDetectionCommand command) throws UnfeasibleOperation {
        var target = new DetectionTarget(command.target().snName(), command.target().pseudo());
        var anomalyType = AnomalyType.valueOf(command.anomalyType().name());
        try {
            client_anomaly_detection.addAnAnomalyDetectionType(new AddAnomalyDetectionEvent(target, anomalyType));
        } catch (IOException e) {
            throw new UnfeasibleOperation("couldn't publish to the Anomaly Detection microservice: " + e.getMessage());
        }
    }

    @Override
    public void addNewMessageAndPerformAnomalyDetection(final AddNewMessageAndDetectAnomalyCommand command)
            throws UnfeasibleOperation {
        var message = command.message();
        var event = new AddNewMessageAndDetectAnomalyEvent(new Message(message.nameSocialNetwork(),
                message.userPseudo(), message.instant(), message.content()));
        try {
            client_anomaly_detection.addNewMessageAndPerformAnomalyDetection(event);
        } catch (IOException e) {
            throw new UnfeasibleOperation("couldn't publish to the Anomaly Detection microservice: " + e.getMessage());
        }
    }

    @Override
    public void close() throws IOException, TimeoutException {
        client_anomaly_detection.close();
    }
}
