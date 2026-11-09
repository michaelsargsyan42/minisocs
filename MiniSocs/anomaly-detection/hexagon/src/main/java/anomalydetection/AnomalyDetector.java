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

import anomalydetection.api.constants.AnomalyType;

/**
 * This interface defines the contract of an anomaly detector.
 * 
 * @author Denis Conan
 */
public interface AnomalyDetector {
	/**
	 * gets the type of the anomaly detector.
	 * 
	 * @return the type.
	 */
	AnomalyType type();
	/**
	 * detects an anomaly.
	 * 
	 * @param content the content to use for the detection.
	 * @return true when detected.
	 */
	boolean check(String content);
}
