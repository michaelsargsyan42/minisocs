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
Contributor(s): J. Paul Gibson (translation to English)
*/

package minisocs.api.port.driven.fordetecting.api;

import minisocs.api.exceptions.UnfeasibleOperation;
import minisocs.api.port.driven.fordetecting.api.command.AddNewMessageAndDetectAnomalyCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.AddAnomalyDetectionCommand;

/**
 * This interface is the API of the software application for detecting anomaly
 * detection.
 * 
 * @author Denis Conan
 */
public interface ForDetecting {
	/**
	 * adds a type of anomaly to detect for messages from a given user.
	 * 
	 * @param command the command.
	 * @throws UnfeasibleOperation in case of problem.
	 */
	void addAnAnomalyDetectionType(AddAnomalyDetectionCommand command) throws UnfeasibleOperation;

	/**
	 * adds a new message and perform anomaly detection.
	 * 
	 * @param command the command.
	 * @throws UnfeasibleOperation in case of problem.
	 */
	void addNewMessageAndPerformAnomalyDetection(AddNewMessageAndDetectAnomalyCommand command) throws UnfeasibleOperation;
}
