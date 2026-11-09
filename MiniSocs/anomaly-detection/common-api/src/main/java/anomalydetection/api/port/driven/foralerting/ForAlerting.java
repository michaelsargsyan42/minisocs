package anomalydetection.api.port.driven.foralerting;

import anomalydetection.api.port.driven.foralerting.events.AnomalyDetectionEvent;
public interface ForAlerting {
    void alert(AnomalyDetectionEvent event);
}