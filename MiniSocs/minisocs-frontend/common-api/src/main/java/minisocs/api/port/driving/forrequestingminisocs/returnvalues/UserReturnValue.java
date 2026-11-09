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

package minisocs.api.port.driving.forrequestingminisocs.returnvalues;

import minisocs.api.constants.NewMessageNotificationStrategy;

/**
 * This record models the information provided when listing a users.
 * 
 * @param pseudo the user's pseudo.
 * @param firstName the user's first name.
 * @param name the user's name.
 * @param email the user's email.
 * @param strategy the user's strategy for the notification of new messages.
 */
public record UserReturnValue(String pseudo, String firstName, String name, String email,
		NewMessageNotificationStrategy strategy) implements ReturnValue {
	/**
	 * constructs a user return value to be serialised. It tests arguments.
	 * 
	 * @param pseudo    the user's pseudo.
	 * @param firstName the user's first name.
	 * @param name      the user's name.
	 * @param email     the user's email.
	 * @param strategy  the user's strategy for the notification of new messages.
	 */
	public UserReturnValue {
		if (pseudo == null || pseudo.isBlank()) {
			throw new IllegalArgumentException("pseudo cannot be null or empty");
		}
		if (firstName == null || firstName.isBlank()) {
			throw new IllegalArgumentException("first name cannot be null or empty");
		}
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("name cannot be null or empty");
		}
		if (email == null || email.isBlank()) {
			throw new IllegalArgumentException("email cannot be null or empty");
		}
		if (strategy == null) {
			throw new IllegalArgumentException("notification strategy cannot be null");
		}
	}

	@Override
	public String toString() {
		return "UserReturnValue [pseudo=" + pseudo + ", firstName=" + firstName + ", name=" + name + ", email=" + email
				+ ", strategy=" + strategy + "]";
	}
}
