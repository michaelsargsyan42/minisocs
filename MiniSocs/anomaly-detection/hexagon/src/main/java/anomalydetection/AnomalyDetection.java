/**
This file is part of the course CSC5002.

The course material is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

The course material is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the course CSC5002.  If not, see <http://www.gnu.org/licenses/>.

Initial developer(s): Denis Conan
Contributor(s):
*/

package anomalydetection;

import static anomalydetection.common.Log.ANOMALY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import anomalydetection.api.constants.AnomalyType;
import anomalydetection.api.exceptions.UnfeasibleOperation;
import anomalydetection.api.port.driven.foralerting.ForAlerting;
import anomalydetection.api.port.driven.foralerting.events.AnomalyDetectionEvent;
import anomalydetection.api.port.driven.forstoringincidents.api.ForStoringIncidentsAPI;
import anomalydetection.api.port.driven.forstoringincidents.model.AnomalyDetectionIncident;
import anomalydetection.api.port.driving.fordetecting.api.AnomalyDetectionAPI;
import anomalydetection.api.port.driving.fordetecting.api.DetectionTarget;
import anomalydetection.api.port.driving.fordetecting.events.AddAnomalyDetectionEvent;
import anomalydetection.api.port.driving.fordetecting.events.AddNewMessageAndDetectAnomalyEvent;

/**
 * This class is the facade of the software application.
 * 
 * @author Denis Conan
 */
public class AnomalyDetection implements AnomalyDetectionAPI {
	/**
	 * the maximum length of the message content kept in an incident: the content
	 * column is a VARCHAR(255), orm.xml setting no length.
	 */
	private static final int MAX_STORED_CONTENT_LENGTH = 255;
	/**
	 * the collection of detectors, per targets.
	 */
	private final Map<DetectionTarget, List<AnomalyDetector>> detectors;
	/**
	 * the driven port for alerting the administrators.
	 */
	private final ForAlerting forAlerting;
	/**
	 * the driven port for storing the incidents.
	 */
	private final ForStoringIncidentsAPI forStoringIncidents;

	/**
	 * constructs the hexagon.
	 * 
	 * @param forAlerting         the reference to the driven adapter for alerting.
	 * @param forStoringIncidents the reference to the driven adapter for storing
	 *                            incidents.
	 */
	public AnomalyDetection(final ForAlerting forAlerting, final ForStoringIncidentsAPI forStoringIncidents) {
		this.detectors = new HashMap<>();
		this.forAlerting = forAlerting;
		this.forStoringIncidents = forStoringIncidents;
	}

	@Override
	public void addAnAnomalyDetectionType(final AddAnomalyDetectionEvent event) throws UnfeasibleOperation {
		ANOMALY.info("{}", () -> event);
		if (event == null) {
			throw new IllegalArgumentException("event cannot be null");
		}
		if (event.target() == null) {
			ANOMALY.warn("{}", () -> "the detection target cannot be null");
		}
		if (event.target().pseudo() == null || event.target().pseudo().isBlank()) {
			ANOMALY.warn("{}", () -> "in command, the pseudo cannot be null or empty");
			return;
		}
		if (event.target().snName() == null || event.target().snName().isBlank()) {
			ANOMALY.warn("{}", () -> "in command, the name of the social network cannot be null or empty");
			return;
		}
		if (event.anomalyType() == null) {
			ANOMALY.warn("{}", () -> "the anomaly type cannot be null");

		}
		var targetDetectors = detectors.get(event.target());
		if (targetDetectors == null) {
			targetDetectors = new ArrayList<AnomalyDetector>();
			detectors.put(event.target(), targetDetectors);
		}
		if (event.anomalyType().equals(AnomalyType.INAPPROPRIATE_VOCABULARY)) {
			targetDetectors.add(new DetectorInappropriateVocabulary());
		} else if (event.anomalyType().equals(AnomalyType.LONG_MESSAGE)) {
			targetDetectors.add(new DetectorLongMessage());
		}
		ANOMALY.info("{}", () -> "End of addAnAnomalyDetectionType");
	}

	@Override
	public void addNewMessageAndPerformAnomalyDetection(final AddNewMessageAndDetectAnomalyEvent event) {
		ANOMALY.info("{}", () -> event);
		if (event == null) {
			throw new IllegalArgumentException("event cannot be null");
		}
		var targetDetectors = detectors
				.get(new DetectionTarget(event.message().nameSocialNetwork(), event.message().userPseudo()));
		if (targetDetectors == null) {
			ANOMALY.trace("{}", () -> "no detection target for (" + event.message().nameSocialNetwork() + ","
					+ event.message().userPseudo() + ")");
		} else {
			var matchingDetectors = targetDetectors.stream().filter(d -> d.check(event.message().content())).toList();
			ANOMALY.info("{}",
					() -> "anomaly detection for " + matchingDetectors.stream().map(d -> d.type().name()).toList());
			for (var detector : matchingDetectors) {
				if (forAlerting != null) {
					forAlerting.alert(new AnomalyDetectionEvent(event.message().userPseudo(),
							event.message().nameSocialNetwork(), event.message().instant(), detector.type()));
				}
				if (forStoringIncidents != null) {
					forStoringIncidents.storeIncident(toIncident(event, detector));
				}
			}
		}
		ANOMALY.info("{}", () -> "End of addNewMessageAndPerformAnomalyDetection");
	}

	/**
	 * builds the incident to store: default constructor and setters (lab 2.c).
	 * 
	 * @param event    the event of the analysed message.
	 * @param detector the detector that found the anomaly.
	 * @return the incident.
	 */
	private static AnomalyDetectionIncident toIncident(final AddNewMessageAndDetectAnomalyEvent event,
			final AnomalyDetector detector) {
		var message = event.message();
		var content = message.content();
		var incident = new AnomalyDetectionIncident();
		incident.setPseudo(message.userPseudo());
		incident.setSnname(message.nameSocialNetwork());
		incident.setInstant(message.instant().toString());
		incident.setType(detector.type().name());
		incident.setContent(content.length() <= MAX_STORED_CONTENT_LENGTH ? content
				: content.substring(0, MAX_STORED_CONTENT_LENGTH));
		return incident;
	}
}
