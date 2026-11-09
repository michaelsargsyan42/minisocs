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

package anomalydetection.api.port.driving.fordetecting.api;

import anomalydetection.api.exceptions.UnfeasibleOperation;
import anomalydetection.api.port.driving.fordetecting.events.AddAnomalyDetectionEvent;
import anomalydetection.api.port.driving.fordetecting.events.AddNewMessageAndDetectAnomalyEvent;

/**
 * This interface is the API of the anomaly detection microservice.
 * 
 * @author Denis Conan
 */
public interface AnomalyDetectionAPI {
	/**
	 * adds a type of anomaly to detect for messages from a given user.
	 * 
	 * @param event the event to consume.
	 * @throws UnfeasibleOperation in case of problem.
	 */
	void addAnAnomalyDetectionType(AddAnomalyDetectionEvent event) throws UnfeasibleOperation;

	/**
	 * adds a new message and perform anomaly detection.
	 * 
	 * @param event the event to consume.
	 */
	void addNewMessageAndPerformAnomalyDetection(AddNewMessageAndDetectAnomalyEvent event);
}
