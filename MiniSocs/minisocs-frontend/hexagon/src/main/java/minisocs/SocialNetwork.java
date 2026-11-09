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

import static minisocs.common.Log.MINISOCS;

import minisocs.common.UtilSerialiser;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import minisocs.api.port.driven.fornotifying.ForRegistering;
import com.fasterxml.jackson.core.JsonProcessingException;
import minisocs.api.port.driven.fornotifying.RequestForModeration;

import minisocs.api.constants.NewMessageNotificationStrategy;
import minisocs.api.exceptions.UnfeasibleOperation;
import minisocs.api.port.driving.forrequestingminisocs.returnvalues.NewMessage;
import minisocs.api.port.driving.forrequestingminisocs.returnvalues.NewMessages;
import minisocs.common.UtilSerialiser;
import minisocs.api.port.driven.fornotifying.Notification;
import minisocs.api.port.driven.fornotifying.TypeNotification;
import minisocs.api.port.driven.fornotifying.NotificationConsumer;

/**
 * This class realizes the concept of social network.
 * 
 * NB: We have chosen to index contributions (as moderators and as members),
 * that is, to use dictionaries with user pseudos as keys. This avoids having to
 * scan through all contributions, e.g., as members, to find a given user's
 * contribution.
 * 
 * @author Denis Conan, J. Paul Gibson (translation to English)
 */
public class SocialNetwork {
	/**
	 * the name of the social network.
	 */
	private String name;
	/**
	 * the state of the social network.
	 */
	private SocialNetworkState snState;
	/**
	 * the members.
	 */
	private Map<String, Participation> members;
	/**
	 * the moderators.
	 */
	private Map<String, Participation> moderators;
	/**
	 * the messages.
	 */
	private Map<Instant, Message> messages;

	/**
	 * construct a social network.
	 * 
	 * @param name the name.
	 * @param user the user who creates the social network.
	 */
	public SocialNetwork(final String name, final User user) {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("name cannot be null or empty");
		}
		if (user == null) {
			throw new IllegalArgumentException("user cannot be null or empty");
		}
		this.name = name;
		this.snState = SocialNetworkState.OPEN;
		NewMessageNotificationStrategy strategy = user.getStrategy();
		Participation p = new Participation(user, this, true, strategy);
		members = new HashMap<>();
		members.put(user.getPseudo(), p);
		moderators = new HashMap<>();
		moderators.put(user.getPseudo(), p);
		messages = new HashMap<>();
		assert invariant();
	}

	/**
	 * checks the class invariant.
	 * 
	 * @return if the invariant is respected.
	 */
	public boolean invariant() {
		return name != null && !name.isBlank() && snState != null && members != null && moderators != null
				&& messages != null;
	}

	/**
	 * establishes whether the user is a moderator of the social network.
	 * 
	 * @param user the user.
	 * @return if the user is a moderator
	 */
	public boolean isModerator(final User user) {
		if (!snState.equals(SocialNetworkState.OPEN)) {
			throw new IllegalStateException("the social network is closed");
		}
		if (user == null) {
			throw new IllegalArgumentException("user cannot be null");
		}
		return moderators.containsKey(user.getPseudo());
	}

	/**
	 * list the moderators of the social network.
	 * 
	 * @return the list of moderators.
	 */
	public List<User> listModerators() {
		if (!snState.equals(SocialNetworkState.OPEN)) {
			throw new IllegalStateException("the social network is not open");
		}
		return moderators.values().stream().map(Participation::getUser).toList();
	}

	/**
	 * establishes whether the user is a member of the social network.
	 * 
	 * @param user the user.
	 * @return if the user is a member.
	 */
	public boolean isMember(final User user) {
		if (!snState.equals(SocialNetworkState.OPEN)) {
			throw new IllegalStateException("the social network is not open");
		}
		if (user == null) {
			throw new IllegalArgumentException("user cannot be null");
		}
		return members.containsKey(user.getPseudo());
	}

	/**
	 * list the members of the social network.
	 * 
	 * @return the list of members.
	 */
	public List<User> listMembers() {
		if (!snState.equals(SocialNetworkState.OPEN)) {
			throw new IllegalStateException("the social network is not open");
		}
		return members.values().stream().map(Participation::getUser).toList();
	}

	/**
	 * closes the social network. The operation is idempotent.
	 */
	public void close() {
		this.snState = SocialNetworkState.CLOSED;
		assert invariant();
	}

	/**
	 * adds a member to the social network.
	 * 
	 * @param moderator           the moderator of the social network.
	 * @param newMember           the new member.
	 * @param notificationManager the notification manager for call to subscribe.
	 * @throws UnfeasibleOperation in case of problem with the preconditions.
	 */
	public void addMember(final User moderator, final User newMember, final ForRegistering notificationManager)
			throws UnfeasibleOperation {
		if (moderator == null) {
			throw new IllegalArgumentException("moderator cannot be null");
		}
		if (newMember == null) {
			throw new IllegalArgumentException("new member cannot be null");
		}
		if (!snState.equals(SocialNetworkState.OPEN)) {
			throw new IllegalStateException("the social network is not open");
		}
		if (!this.isModerator(moderator)) {
			throw new UnfeasibleOperation(moderator.getPseudo() + " is not a moderator");
		}
		if (this.isMember(newMember)) {
			throw new UnfeasibleOperation(newMember + " is already a member");
		}
		NewMessageNotificationStrategy strategy = newMember.getStrategy();
		members.put(newMember.getPseudo(), new Participation(newMember, this, false, strategy));
		if (notificationManager != null) {
			notificationManager.addMemberToSocialNetwork(name, newMember.getPseudo(), newMember.getStrategy());
		}
		assert invariant();
	}

	/**
	 * post a message in this social network.
	 * 
	 * @param member              the member who adds it.
	 * @param content             the content of the message.
	 * @param instant             the moment the message was created.
	 * @param notificationManager the notification manager for the calls to submit.
	 * @throws UnfeasibleOperation     in case of problems with the preconditions.
	 * @throws JsonProcessingException in case of problem when serialising the
	 *                                 moderation request or the new message.
	 */
	public void postMessage(final User member, final String content, final Instant instant,
			final ForRegistering notificationManager) throws UnfeasibleOperation, JsonProcessingException {
		if (member == null) {
			throw new IllegalArgumentException("member cannot be null");
		}
		if (content == null || content.isBlank()) {
			throw new IllegalArgumentException("content cannot be null or empty");
		}
		if (instant == null) {
			throw new IllegalArgumentException("instant cannot be null");
		}
		if (!snState.equals(SocialNetworkState.OPEN)) {
			throw new IllegalStateException("the social network is not open");
		}
		Participation p = members.get(member.getPseudo());
		if (p == null) {
			throw new UnfeasibleOperation(member.getPseudo() + " is not a member of the social network");
		}
		if (isModerator(member)) {
			messages.put(instant, new Message(p, content, instant, MessageState.VISIBLE));
			members.values().stream().filter(p1 -> p1.getStrategy().equals(NewMessageNotificationStrategy.IMMEDIATE))
					.forEach(p2 -> p2.updateInstantLastMessageRead());
			if (notificationManager != null) {
				notificationManager.notify(name,
						new Notification(TypeNotification.NEW_MESSAGE, UtilSerialiser
								.writeValueAsString(new NewMessages(new NewMessage(name, instant, content)))));
			}
		} else {
			messages.put(instant, new Message(p, content, instant, MessageState.AWAITING_MODERATION));
			if (notificationManager != null) {
				notificationManager.notify(name,
						new Notification(TypeNotification.MODERATION_REQUEST, UtilSerialiser
								.writeValueAsString(new RequestForModeration(name, instant))));
			}
		}
		assert invariant();
	}

	/**
	 * moderates a message.
	 * 
	 * @param moderator           the moderator user who moderates the message.
	 * @param instant             the moment the message was created, used to find
	 *                            it.
	 * @param decision            the moderation decision.
	 * @param notificationManager the notification manager for the calls to submit.
	 * @throws UnfeasibleOperation     in case of problems with the preconditions.
	 * @throws JsonProcessingException in case of problem when serialising the new
	 *                                 message.
	 */
	public void moderateMessage(final User moderator, final Instant instant, final boolean decision,
			final ForRegistering notificationManager) throws UnfeasibleOperation, JsonProcessingException {
		if (moderator == null) {
			throw new IllegalArgumentException("moderator cannot be null");
		}
		if (instant == null) {
			throw new IllegalArgumentException("instant cannot be null");
		}
		if (!snState.equals(SocialNetworkState.OPEN)) {
			throw new IllegalStateException("the social network is not open");
		}
		Participation p = moderators.get(moderator.getPseudo());
		if (p == null) {
			throw new UnfeasibleOperation(moderator.getPseudo() + " is not a moderator");
		}
		Message msg = messages.get(instant);
		if (msg == null) {
			throw new UnfeasibleOperation("no message posted in this social network at this time");
		}
		if (!msg.getState().equals(MessageState.AWAITING_MODERATION)) {
			throw new UnfeasibleOperation("the message is not awaiting moderation (" + msg.getState() + ")");
		}
		msg.moderate(decision);
		if (decision) {
			members.values().stream().filter(p1 -> p1.getStrategy().equals(NewMessageNotificationStrategy.IMMEDIATE))
					.forEach(p2 -> p2.updateInstantLastMessageRead());
			if (notificationManager != null) {
				notificationManager.notify(name,
						new Notification(TypeNotification.NEW_MESSAGE, UtilSerialiser
								.writeValueAsString(new NewMessage(name, instant, msg.getContent()))));
			}
		}
		assert invariant();
	}

	/**
	 * read unread messages.
	 * 
	 * @param member the member of the social network.
	 * @return the list of new messages.
	 * @throws UnfeasibleOperation in case of problem with the preconditions.
	 */
	public NewMessages readUnreadMessages(final User member) throws UnfeasibleOperation {
		if (!snState.equals(SocialNetworkState.OPEN)) {
			throw new IllegalStateException("the social network is not open");
		}
		if (member == null) {
			throw new IllegalArgumentException("member cannot be null");
		}
		Participation p = members.get(member.getPseudo());
		if (p == null) {
			throw new UnfeasibleOperation(member.getPseudo() + " is not a member");
		}
		Instant lastRead = p.getInstantLastMessageRead();
		p.updateInstantLastMessageRead();
		var newmsgs = new NewMessages(messages.values().stream()
				.filter(m -> m.getState().equals(MessageState.VISIBLE) && m.getInstant().isAfter(lastRead))
				.map(m -> new NewMessage(name, m.getInstant(), m.getContent())).toList().toArray(new NewMessage[0]));
		MINISOCS.debug("{}",
				() -> "readUnreadMessages returns " + newmsgs.newMessages().length
						+ " new messages; previous / current instant of last message read: ("
						+ UtilSerialiser.formatInstantIntoString(lastRead) + " / "
						+ UtilSerialiser.formatInstantIntoString(Instant.now()) + ")");
		return newmsgs;
	}

	/**
	 * emulates the end of the day.
	 * 
	 * @param notificationManager the notification manager used to notify.
	 * @throws JsonProcessingException in case of problem when serialising new
	 *                                 messages.
	 */
	public void emulateEndOfDay(final ForRegistering notificationManager) throws JsonProcessingException {
		if (!snState.equals(SocialNetworkState.OPEN)) {
			throw new IllegalStateException("the social network is not open");
		}
		for (Participation p : members.values()) {
			if (p.getStrategy().equals(NewMessageNotificationStrategy.DAILY)) {
				Instant instant = LocalDateTime.now().with(LocalTime.MIN).toInstant(ZoneOffset.UTC);
				NewMessages msgs = new NewMessages(messages.values().stream()
						.filter(m -> m.getInstant().isAfter(instant) && m.getState().equals(MessageState.VISIBLE))
						.map(m -> new NewMessage(name, m.getInstant(), m.getContent())).toList()
						.toArray(new NewMessage[0]));
				if (notificationManager != null) {
					notificationManager.notify(name, new Notification(TypeNotification.NEW_MESSAGES,
							UtilSerialiser.writeValueAsString(msgs)));
				}
			}
		}
		assert invariant();
	}

	/**
	 * gets the name of the social network.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * the state of the social network.
	 * 
	 * @return the state.
	 */
	public SocialNetworkState getSocialNetworkState() {
		return snState;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof SocialNetwork)) {
			return false;
		}
		SocialNetwork other = (SocialNetwork) obj;
		return Objects.equals(name, other.name);
	}

	@Override
	public String toString() {
		return "SocialNetwork [name=" + name + ", state=" + snState + "]";
	}
}
