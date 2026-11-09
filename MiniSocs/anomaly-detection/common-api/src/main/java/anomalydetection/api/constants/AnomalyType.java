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

package anomalydetection.api.constants;

/**
 * This enumerated type models the types of anomalies.
 * 
 * <p>
 * Every type has a corresponding detector, which is a predicate that is
 * evaluated on a message content.
 * 
 * @author Denis Conan
 * 
 */
public enum AnomalyType {
	/**
	 * the anomaly type for inappropriate vocabulary.
	 */
	INAPPROPRIATE_VOCABULARY("inappropriate vocabulary"),
	/**
	 * the anomaly type for too long messages.
	 */
	LONG_MESSAGE("long message");

	/**
	 * the name of the anomaly to display.
	 */
	private String name;

	/**
	 * builds an enumerator.
	 * 
	 * @param name the name of the anomaly type.
	 */
	AnomalyType(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
