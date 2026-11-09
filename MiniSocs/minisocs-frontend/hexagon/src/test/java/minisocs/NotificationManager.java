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

package minisocs;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.SubmissionPublisher;
import minisocs.api.port.driven.fornotifying.ForRegistering;

import minisocs.api.port.driven.fornotifying.Notification;
import minisocs.api.port.driven.fornotifying.NotificationConsumer;
import minisocs.api.port.driven.fornotifying.TypeNotification;

import minisocs.api.constants.NewMessageNotificationStrategy;
import minisocs.api.exceptions.UnfeasibleOperation;

/**
 * This class implements the notification of moderation requests and new
 * messages using the Java Flow API.
 */
public class NotificationManager implements ForRegistering {
	/**
	 * the producers of moderation messages.
	 */
	private Map<String, SubmissionPublisher<Notification>> producersForModeration;
	/**
	 * the immediate producer of posted and accepted messages.
	 */
	private Map<String, SubmissionPublisher<Notification>> producersForImmediateNotif;
	/**
	 * the daily producer of posted and accepted messages.
	 */
	private Map<String, SubmissionPublisher<Notification>> producersForDailyNotif;
	/**
	 * the consumers of the users for receiving moderation request notifications
	 * with the JAVA Flow API.
	 */
	private Map<String, FlowApiNotificationConsumer> userConsumersForModerationRequests;
	/**
	 * the consumers of the users for receiving new message notifications with the
	 * JAVA Flow API.
	 */
	private Map<String, FlowApiNotificationConsumer> userConsumersForNewMessages;

	/**
	 * builds an instance of the notification manager.
	 */
	public NotificationManager() {
		this.producersForModeration = new HashMap<>();
		this.producersForImmediateNotif = new HashMap<>();
		this.producersForDailyNotif = new HashMap<>();
		this.userConsumersForModerationRequests = new HashMap<>();
		this.userConsumersForNewMessages = new HashMap<>();
	}

	/**
	 * the invariant.
	 * 
	 * @return {@code true} if the invariant is respected.
	 */
	public boolean invariant() {
		return producersForModeration != null && producersForImmediateNotif != null && producersForDailyNotif != null
				&& userConsumersForNewMessages != null;
	}

	/**
	 * adds a user consumer object to prepare the receipt of moderation requests.
	 * 
	 * @param userPseudo the pseudo of the user that is added to the social network.
	 * @param consumer   the object consumer provided by the user to prepare the
	 *                   receipt of notifications.
	 * @throws UnfeasibleOperation in case of problem with the preconditions.
	 */
	@Override
	public void addUserConsumerObjectForModerationRequests(final String userPseudo, final NotificationConsumer consumer)
			throws UnfeasibleOperation {
		if (userPseudo == null || userPseudo.isBlank()) {
			throw new IllegalArgumentException("userPseudo cannot be null");
		}
		if (consumer == null) {
			throw new IllegalArgumentException("consumer cannot be null");
		}
		if (!(consumer instanceof FlowApiNotificationConsumer cons)) {
			throw new IllegalArgumentException(
					"consumer not instance of " + FlowApiNotificationConsumer.class.getName());
		}
		userConsumersForModerationRequests.put(userPseudo, cons);
	}

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
	@Override
	public void addUserConsumerObjectForNewMessages(final String userPseudo,
			final NewMessageNotificationStrategy strategy, final NotificationConsumer consumer)
			throws UnfeasibleOperation {
		if (userPseudo == null || userPseudo.isBlank()) {
			throw new IllegalArgumentException("userPseudo cannot be null");
		}
		if (strategy == null) {
			throw new IllegalArgumentException("strategy cannot be null");
		}
		if (consumer == null) {
			throw new IllegalArgumentException("consumer cannot be null");
		}
		if (!(consumer instanceof FlowApiNotificationConsumer cons)) {
			throw new IllegalArgumentException(
					"consumer not instance of " + FlowApiNotificationConsumer.class.getName());
		}
		userConsumersForNewMessages.put(userPseudo, cons);
	}

	/**
	 * creates the submission publishers and the subscriptions for a new social
	 * network.
	 * 
	 * @param snName   the name of the social network.
	 * @param user     the user that creates the social network.
	 * @param strategy the notification strategy of the user creator.
	 * @throws UnfeasibleOperation in case of problem with the preconditions.
	 */
	@Override
	public void createSocialNetwork(final String snName, final String userPseudo,
			final NewMessageNotificationStrategy strategy) throws UnfeasibleOperation {
		if (snName == null || snName.isBlank()) {
			throw new IllegalArgumentException("snName cannot be null");
		}
		if (userPseudo == null || userPseudo.isBlank()) {
			throw new IllegalArgumentException("userPseudo cannot be null");
		}
		if (strategy == null) {
			throw new IllegalArgumentException("strategy cannot be null");
		}
		var consumerModeration = userConsumersForModerationRequests.get(userPseudo);
		if (consumerModeration == null) {
			throw new UnfeasibleOperation(
					"there is no consumer object for the user (" + userPseudo + ") to receive moderation requests");
		}
		var producerForModeration = new SubmissionPublisher<Notification>();
		producersForModeration.put(snName, producerForModeration);
		producerForModeration.subscribe(consumerModeration);
		var consumer = userConsumersForNewMessages.get(userPseudo);
		if (consumer == null) {
			throw new UnfeasibleOperation(
					"there is no consumer object for the user (" + userPseudo + ") to receive new messages");
		}
		var producerForImmediateNotif = new SubmissionPublisher<Notification>();
		producersForImmediateNotif.put(snName, producerForImmediateNotif);
		var producerForDailyNotif = new SubmissionPublisher<Notification>();
		producersForDailyNotif.put(snName, producerForDailyNotif);
		if (strategy.equals(NewMessageNotificationStrategy.IMMEDIATE)) {
			producerForImmediateNotif.subscribe(consumer);
		} else if (strategy.equals(NewMessageNotificationStrategy.DAILY)) {
			producerForDailyNotif.subscribe(consumer);
		}
	}

	/**
	 * add a member to the social network.
	 * 
	 * @param snName     the name of the social network.
	 * @param userPseudo the pseudo of the user that is added to the social network.
	 * @param strategy   the notification strategy of the new user.
	 */
	@Override
	public void addMemberToSocialNetwork(final String snName, final String userPseudo,
			final NewMessageNotificationStrategy strategy) {
		if (snName == null || snName.isBlank()) {
			throw new IllegalArgumentException("snName cannot be null");
		}
		if (userPseudo == null || userPseudo.isBlank()) {
			throw new IllegalArgumentException("userPseudo cannot be null");
		}
		if (strategy == null) {
			throw new IllegalArgumentException("strategy cannot be null");
		}
		FlowApiNotificationConsumer consumer = userConsumersForNewMessages.get(userPseudo);
		if (consumer == null) {
			throw new IllegalStateException("no consumer for user " + userPseudo);
		}
		if (strategy.equals(NewMessageNotificationStrategy.IMMEDIATE)) {
			producersForImmediateNotif.get(snName).subscribe(consumer);
		} else if (strategy.equals(NewMessageNotificationStrategy.DAILY)) {
			producersForDailyNotif.get(snName).subscribe(consumer);
		}
	}

	/**
	 * sends a notification, either a request for moderation or a message or even a
	 * set of messages.
	 * 
	 * @param snName       the name of the social network.
	 * @param notification
	 */
	@Override
	public void notify(final String snName, final Notification notification) {
		if (snName == null || snName.isBlank()) {
			throw new IllegalArgumentException("snName cannot be null");
		}
		if (notification == null) {
			throw new IllegalArgumentException("notification cannot be null");
		}
		SubmissionPublisher<Notification> publisher = null;
		switch (notification.type()) {
		case TypeNotification.MODERATION_REQUEST:
			publisher = this.producersForModeration.get(snName);
			if (publisher != null) {
				publisher.submit(notification);
			}
			break;
		case TypeNotification.NEW_MESSAGE:
			publisher = this.producersForImmediateNotif.get(snName);
			if (publisher != null) {
				publisher.submit(notification);
			}
			break;
		case TypeNotification.NEW_MESSAGES:
			publisher = this.producersForDailyNotif.get(snName);
			if (publisher != null) {
				publisher.submit(notification);
			}
			break;
		default: // nop
		}
	}
}
