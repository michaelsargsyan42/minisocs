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

package minisocs.api.port.driven.fornotifying;

import minisocs.api.constants.NewMessageNotificationStrategy;
import minisocs.api.exceptions.UnfeasibleOperation;

/**
 * This interface defines the methods for registering to notifications.
 */
public interface ForRegistering {
	/**
	 * adds a user consumer object to prepare the receipt of moderation requests.
	 * 
	 * @param userPseudo the pseudo of the user that is added to the social network.
	 * @param consumer   the object consumer provided by the user to prepare the
	 *                   receipt of notifications.
	 * @throws UnfeasibleOperation in case of problem with the preconditions.
	 */
	void addUserConsumerObjectForModerationRequests(String userPseudo, NotificationConsumer consumer)
			throws UnfeasibleOperation;

	/**
	 * adds a user consumer object to prepare the receipt of new message
	 * notifications.
	 * 
	 * @param userPseudo the pseudo of the user that is added to the social network.
	 * @param strategy   the notification strategy of the new user.
	 * @param consumer   the object consumer provided by the user to prepare the
	 *                   receipt of notifications.
	 * @throws UnfeasibleOperation in case of problem with the preconditions.
	 */
	void addUserConsumerObjectForNewMessages(String userPseudo, NewMessageNotificationStrategy strategy,
			NotificationConsumer consumer) throws UnfeasibleOperation;

	/**
	 * creates the submission publishers and the subscriptions for a new social
	 * network.
	 * 
	 * @param snName   the name of the social network.
	 * @param user     the user that creates the social network.
	 * @param strategy the notification strategy of the user creator.
	 * @throws UnfeasibleOperation in case of problem with the preconditions.
	 */
	void createSocialNetwork(String snName, String user, NewMessageNotificationStrategy strategy)
			throws UnfeasibleOperation;

	/**
	 * add a member to the social network.
	 * 
	 * @param snName     the name of the social network.
	 * @param userPseudo the pseudo of the user that is added to the social network.
	 * @param strategy   the notification strategy of the new user.
	 */
	void addMemberToSocialNetwork(String snName, String userPseudo, NewMessageNotificationStrategy strategy);

	/**
	 * sends a notification, either a request for moderation or a message or even a
	 * set of messages.
	 * 
	 * @param snName       the name of the social network.
	 * @param notification
	 */
	void notify(String snName, Notification notification);
}
