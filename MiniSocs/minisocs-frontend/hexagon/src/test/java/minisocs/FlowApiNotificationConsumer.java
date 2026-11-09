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

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import minisocs.api.port.driven.fornotifying.Notification;
import minisocs.api.port.driven.fornotifying.NotificationConsumer;
import minisocs.api.port.driven.fornotifying.TypeNotification;

/**
 * This class defines the notifications consumer to the user.
 * 
 * @author Denis Conan, J. Paul Gibson (translation to English)
 */
public class FlowApiNotificationConsumer implements NotificationConsumer, Subscriber<Notification> {
	/**
	 * the subscription.
	 */
	private Subscription subscription;
	/**
	 * the user's pseudo.
	 */
	private String userPseudo;
	/**
	 * the number of notifications received. This attribute is used by tests:
     * It is reset to 0 before a test and used to verify a post-condition.
	 */
	private static int nbNotificationsReceived = 0;

	/**
	 * builds the consumer.
	 * 
	 * @param userPseudo the user's pseudo.
	 */
	public FlowApiNotificationConsumer(final String userPseudo) {
		this.userPseudo = userPseudo;
	}

	/**
	 * gets the number of notifications received.
	 * 
	 * @return the number of notifications received.
	 */
	public static int getNbNotificationsReceived() {
		return nbNotificationsReceived;
	}

	/**
	 * resets to 0 the number of notifications received (for example before a test).
	 */
	public static void resetNbNotificationsReceived() {
		nbNotificationsReceived = 0;
	}

	/**
	 * increments the counter {@link #nbNotificationsReceived}.
	 */
	private static void incrementNbNotificationsReceived() {
		nbNotificationsReceived++;
	}

	@Override
	public void onSubscribe(final Subscription subscription) {
		this.subscription = subscription;
		subscription.request(1);
	}

	@Override
	public void onNext(final Notification notification) {
		System.out.println(userPseudo + ", new notification : type = " + notification.type()
				+ "; contents = " + notification.content());
		incrementNbNotificationsReceived();
		subscription.request(1);
	}

	@Override
	public void onError(final Throwable throwable) {
		throwable.printStackTrace();
	}

	@Override
	public void onComplete() {
		System.out.println("end of notification subscription");
	}

	@Override
	public void onEvent(final Notification notification) {
		this.onNext(notification);
	}
}
