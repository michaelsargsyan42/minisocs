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

import java.util.Objects;

/**
 * This interface models the concept of command of the API.
 */
public sealed interface Command permits AddUserCommand, DeactivateUserAccountCommand, CreateSocialNetworkCommand,
		ListModeratorsSocialNetworkCommand, CloseSocialNetworkCommand, AddMemberToSocialNetworkCommand,
		ListMembersSocialNetworkCommand, PostMessageCommand, ModerateMessageCommand, ReadLatestMessagesCommand,
		AddAnomalyDetectionCommand {
	/**
	 * Verifies that the command is not null.
	 * 
	 * @param command the command.
	 */
	static void validateNotNulCommand(final Command command) {
		Objects.requireNonNull(command, "command cannot be null");
	}
}
