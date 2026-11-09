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

package anomalydetection.api.port.driven.foralerting.events;

import java.time.Instant;

import anomalydetection.api.constants.AnomalyType;

/**
 * This record models the information provided when detecting an anomaly.
 * 
 * @param pseudo      the user's pseudo.
 * @param snName      the name of the social network.
 * @param instant     the creation instant of the message.
 * @param anomalyType the type of the detected anomaly.
 */
public record AnomalyDetectionEvent(String pseudo, String snName, Instant instant, AnomalyType anomalyType)
		implements Event {
	@Override
	public String toString() {
		return "AnomalyDetectionProducedEvent [pseudo=" + pseudo + ", snName=" + snName + ", instant=" + instant
				+ ", anomalyType=" + anomalyType + "]";
	}
}
