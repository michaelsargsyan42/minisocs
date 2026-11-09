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

package anomalydetection.api.port.driven.forstoringincidents.api;

import java.util.List;

import anomalydetection.api.port.driven.forstoringincidents.model.AnomalyDetectionIncident;

/**
 * This interface is the API of the driven port to store incidents.
 * 
 * @author Denis Conan
 */
public interface ForStoringIncidentsAPI {
	
	/**
	 * stores an incident.
	 * 
	 * @param incident the incident to store.
	 */
	void storeIncident(AnomalyDetectionIncident incident);
	
	/**
	 * gets the incidents of a user in a social network.
	 * 
	 * @param pseudo the user's pseudo.
	 */
	List<AnomalyDetectionIncident> getIncidentsOfUser(String pseudo);
	
	/**
	 * gets all the incidents.
	 */
	List<AnomalyDetectionIncident> getIncidents();
	
	/**
	 * gets the incidents in a social network.
	 * 
	 * @param snName the name of the social network.
	 */
	List<AnomalyDetectionIncident> getIncidentsInSocialNetwork(String snName);
}
