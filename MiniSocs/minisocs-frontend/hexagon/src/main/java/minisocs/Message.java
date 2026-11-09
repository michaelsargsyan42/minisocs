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

import java.time.Instant;
import java.util.Objects;

import minisocs.common.UtilSerialiser;

/**
 * This class realizes the concept of a social network message.
 * 
 * @author Denis Conan, J. Paul Gibson (translation to English)
 */
public class Message {
	/**
	 * the user, as a member of the social network, who posted the message.
	 */
	private final Participation participation;
	/**
	 * the moment the message was created.
	 */
	private final Instant instant;
	/**
	 * the content of the message.
	 */
	private final String content;
	/**
	 * the state of the message.
	 */
	private MessageState state;

	/**
	 * builds a message.
	 * 
	 * @param participation the participation of the user who posted the message.
	 * @param content       the content of the message.
	 * @param instant       the time the message was created (before being posted).
	 * @param state         the message's status at the time of creation (directly
	 *                      visible when the user who posted it is a moderator, and
	 *                      awaiting moderation if the user is a member).
	 */
	public Message(final Participation participation, final String content, final Instant instant,
			final MessageState state) {
		if (participation == null) {
			throw new IllegalArgumentException("participation cannot be null");
		}
		if (content == null || content.isBlank()) {
			throw new IllegalArgumentException("content cannot be null or empty");
		}
		if (instant == null) {
			throw new IllegalArgumentException("instant cannot be null");
		}
		if (state == null) {
			throw new IllegalArgumentException("state cannot be null");
		}
		if (!state.equals(MessageState.VISIBLE) && !state.equals(MessageState.AWAITING_MODERATION)) {
			throw new IllegalArgumentException("state can only be visible or awaiting moderation");
		}
		this.participation = participation;
		this.content = content;
		this.instant = instant;
		this.state = state;
		assert invariant();
	}

	/**
	 * checks the class invariant.
	 * 
	 * @return if the invariant is respected.
	 */
	public boolean invariant() {
		return participation != null && content != null && !content.isBlank() && instant != null && state != null;
	}

	/**
	 * moderates a message.
	 * 
	 * @param decision the decision to be implemented.
	 */
	public void moderate(final boolean decision) {
		if (!state.equals(MessageState.AWAITING_MODERATION)) {
			throw new IllegalStateException("message pas en attente de modération");
		}
		state = decision ? MessageState.VISIBLE : MessageState.NOT_ACCEPTED;
		assert invariant();
	}

	/**
	 * gets participation.
	 * 
	 * @return la participation.
	 */
	public Participation getParticipation() {
		return participation;
	}

	/**
	 * obtains the moment of creation.
	 * 
	 * @return l'instant.
	 */
	public Instant getInstant() {
		return instant;
	}

	/**
	 * gets the content.
	 * 
	 * @return the content.
	 */
	public String getContent() {
		return content;
	}

	/**
	 * gets the state.
	 * 
	 * @return the state.
	 */
	public MessageState getState() {
		return state;
	}

	@Override
	public int hashCode() {
		return Objects.hash(content, state, instant, participation);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Message)) {
			return false;
		}
		Message other = (Message) obj;
		return Objects.equals(content, other.content) && state == other.state && Objects.equals(instant, other.instant)
				&& Objects.equals(participation, other.participation);
	}

	@Override
	public String toString() {
		return "Message [participation=" + participation.getUser().getPseudo() + ", instant="
				+ UtilSerialiser.formatInstantIntoString(instant) + ", content=" + content + ", state=" + state + "]";
	}
}
