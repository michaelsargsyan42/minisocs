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

package minisocs;

import java.util.List;
import minisocs.api.port.driving.forrequestingminisocs.commands.AddAnomalyDetectionCommand;
import minisocs.api.port.driven.fornotifying.Notification;
import minisocs.api.port.driven.fornotifying.NotificationConsumer;
import minisocs.api.port.driven.fornotifying.TypeNotification;
import minisocs.api.exceptions.UnfeasibleOperation;
import minisocs.api.port.driving.forrequestingminisocs.commands.AddMemberToSocialNetworkCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.AddUserCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.CloseSocialNetworkCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.CreateSocialNetworkCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.DeactivateUserAccountCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.ListMembersSocialNetworkCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.ListModeratorsSocialNetworkCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.ModerateMessageCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.PostMessageCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.ReadLatestMessagesCommand;
import minisocs.api.port.driving.forrequestingminisocs.returnvalues.NewMessages;
import minisocs.api.port.driving.forrequestingminisocs.returnvalues.SocialNetworkReturnValue;
import minisocs.api.port.driving.forrequestingminisocs.returnvalues.UserReturnValue;

/**
 * This interface is the API of the software application.
 * 
 * @author Denis Conan, J. Paul Gibson (translation to English)
 */
public interface MiniSocsAPI {
	/**
	 * adds a user.
	 * 
	 * @param command the command.
	 * @throws UnfeasibleOperation in case of problem with the preconditions.
	 */
	void addUser(AddUserCommand command) throws UnfeasibleOperation;

	/**
	 * adds a user consumer object to prepare the receipt of moderation request
	 * notifications.
	 * 
	 * @param userPseudo the pseudo of the user that is added to the social network.
	 * @param consumer   the notification consumer provided by the user to prepare
	 *                   the receipt of notifications.
	 * @throws UnfeasibleOperation in case of problem with the preconditions.
	 */
	void addUserConsumerObjectForModerationRequests(String userPseudo, NotificationConsumer consumer)
			throws UnfeasibleOperation;

	/**
	 * adds a user consumer object to prepare the receipt of new message
	 * notifications.
	 * 
	 * @param userPseudo the pseudo of the user that is added to the social network.
	 * @param consumer   the notification consumer provided by the user to prepare
	 *                   the receipt of notifications.
	 * @throws UnfeasibleOperation in case of problem with the preconditions.
	 */
	void addUserConsumerObjectForNewMessages(String userPseudo, NotificationConsumer consumer)
			throws UnfeasibleOperation;

	/**
	 * list the users.
	 * 
	 * @return the list of user return values.
	 */
	List<UserReturnValue> listUsers();

	/**
	 * deactivate a user's account.
	 * 
	 * @param command the command.
	 * @throws UnfeasibleOperation in case of problems with the preconditions.
	 */
	void deactivateUserAccount(DeactivateUserAccountCommand command) throws UnfeasibleOperation;

	/**
	 * creates a social network.
	 * 
	 * @param command the command.
	 * @throws UnfeasibleOperation in case of problems with the preconditions.
	 */
	void createSocialNetwork(CreateSocialNetworkCommand command) throws UnfeasibleOperation;

	/**
	 * list the social networks.
	 * 
	 * @return a list of social network return values.
	 */
	List<SocialNetworkReturnValue> listSocialNetworks();

	/**
	 * list the moderators of a social network.
	 * 
	 * @param command the command
	 * @return the list of moderators, as user return values.
	 * @throws UnfeasibleOperation in case of problems with the preconditions.
	 */
	List<UserReturnValue> listModeratorsSocialNetwork(ListModeratorsSocialNetworkCommand command)
			throws UnfeasibleOperation;

	/**
	 * closes a social network.
	 * 
	 * @param command the command.
	 * @throws UnfeasibleOperation in case of problems with the preconditions.
	 */
	void closeSocialNetwork(CloseSocialNetworkCommand command) throws UnfeasibleOperation;

	/**
	 * adds a member to a social network.
	 * 
	 * @param command the command.
	 * @throws UnfeasibleOperation in case of problems with the preconditions.
	 */
	void addMemberToSocialNetwork(AddMemberToSocialNetworkCommand command) throws UnfeasibleOperation;

	/**
	 * lists the members of a social network.
	 * 
	 * @param command the command.
	 * @return a list of members, as user return values.
	 * @throws UnfeasibleOperation in case of problem with the preconditions.
	 */
	List<UserReturnValue> listMembersSocialNetwork(ListMembersSocialNetworkCommand command) throws UnfeasibleOperation;

	/**
	 * posts a message, meaning a user posts a message on a social network.
	 * 
	 * @param command the command.
	 * @throws UnfeasibleOperation in case of problems with the preconditions.
	 */
	void postMessage(PostMessageCommand command) throws UnfeasibleOperation;

	/**
	 * moderates a message from a social network.
	 * 
	 * @param command the command.
	 * @throws UnfeasibleOperation in case of problems with the preconditions.
	 */
	void moderateMessage(ModerateMessageCommand command) throws UnfeasibleOperation;

	/**
	 * read the latest social media messages not yet read by the user.
	 * 
	 * @param command the command.
	 * @return the list of new unread messages.
	 * @throws UnfeasibleOperation in case of problems with the preconditions.
	 */
	NewMessages readLatestMessages(ReadLatestMessagesCommand command) throws UnfeasibleOperation;


	/**
	 * adds a type of anomaly to detect for the messages of a user in a social network
	 */
	void addAnAnomalyDetectionType(AddAnomalyDetectionCommand command) throws UnfeasibleOperation;

	/**
	 * emulates the end of the day.
	 */
	void emulateEndOfDay() throws UnfeasibleOperation;
}
