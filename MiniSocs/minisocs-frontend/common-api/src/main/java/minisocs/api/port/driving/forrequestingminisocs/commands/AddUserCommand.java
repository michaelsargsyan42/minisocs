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

package minisocs.api.port.driving.forrequestingminisocs.commands;

import minisocs.api.constants.NewMessageNotificationStrategy;

/**
 * This record is the command for adding a user.
 * 
 * @param pseudo    the user's pseudo.
 * @param name      the user's name.
 * @param firstName the user's first name.
 * @param email     the user's email.
 * @param strategy  the notification strategy.
 */
public record AddUserCommand(String pseudo, String name, String firstName, String email,
		NewMessageNotificationStrategy strategy) implements Command {

	@Override
	public String toString() {
		return "AddUser [pseudo=" + pseudo + ", name=" + name + ", firstName=" + firstName + ", email=" + email
				+ ", strategy=" + strategy + "]";
	}
}
