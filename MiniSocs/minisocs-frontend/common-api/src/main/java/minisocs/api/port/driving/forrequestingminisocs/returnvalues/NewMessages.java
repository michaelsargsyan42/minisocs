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

package minisocs.api.port.driving.forrequestingminisocs.returnvalues;

import java.util.Arrays;

/**
 * This record realizes the concept of a list of new messages.
 * 
 * @author Denis Conan, J. Paul Gibson (translation to English)
 * 
 * @param newMessages the new messages.
 */
public record NewMessages(NewMessage... newMessages) {
	/**
	 * constructs a set of new messages by checking preconditions on
     * the arguments.
	 * 
	 * @param newMessages the new messages.
	 */
	public NewMessages {
		if (newMessages == null) {
			throw new IllegalArgumentException("the collection cannot be null");
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(newMessages);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof NewMessages)) {
			return false;
		}
		NewMessages other = (NewMessages) obj;
		return Arrays.equals(newMessages, other.newMessages);
	}

	@Override
	public String toString() {
		return Arrays.toString(newMessages);
	}
}
