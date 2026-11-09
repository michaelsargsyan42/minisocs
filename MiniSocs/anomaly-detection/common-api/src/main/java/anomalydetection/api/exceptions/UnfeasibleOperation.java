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

package anomalydetection.api.exceptions;

/**
 * Functional problem.
 * 
 * @author Denis Conan, J. Paul Gibson (translation to English)
 */
public class UnfeasibleOperation extends Exception {
	/** serial number. */
	private static final long serialVersionUID = 1L;

	/**
	 * constructs an exception with a message.
	 * 
	 * @param message Error message.
	 */
	public UnfeasibleOperation(final String message) {
		super(message);
	}
}
