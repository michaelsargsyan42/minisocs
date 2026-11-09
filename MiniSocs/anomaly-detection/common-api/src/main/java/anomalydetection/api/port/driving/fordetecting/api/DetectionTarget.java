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

import java.util.Objects;

/**
 * This class defines the concept of detection target: the finest granularity is
 * per user in a given social network.
 * 
 * @param snName the name of the social network.
 * @param pseudo the user's pseudo.
 */
public record DetectionTarget(String snName, String pseudo) {
	@Override
	public int hashCode() {
		return Objects.hash(pseudo, snName);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof DetectionTarget(var s, var p))) {
			return false;
		}
		return Objects.equals(snName, s) && Objects.equals(pseudo, p);
	}

	@Override
	public String toString() {
		return "DetectionTarget [snName=" + snName + ", pseudo=" + pseudo + "]";
	}
}
