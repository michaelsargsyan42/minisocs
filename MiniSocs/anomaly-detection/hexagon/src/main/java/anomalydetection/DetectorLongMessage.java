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
 * This class realises the detector of long messages.
 * 
 * @author Denis Conan
 */
public final class DetectorLongMessage implements AnomalyDetector {
	/**
	 * the threshold, in terms of characters, to declare a message as being long.
	 */
	private static final int LONG = 200;

	/**
	 * empty constructor.
	 */
	public DetectorLongMessage() {
		super();
	}
	
	@Override
	public AnomalyType type() {
		return AnomalyType.LONG_MESSAGE;
	}

	@Override
	public boolean check(final String content) {
		return content.length() > LONG;
	}
}
