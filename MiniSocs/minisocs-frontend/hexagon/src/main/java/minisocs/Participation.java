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

import minisocs.api.constants.NewMessageNotificationStrategy;
import minisocs.common.UtilSerialiser;

/**
 * This class implements the concept of participation in a social network,
 * whether as a member or as a moderator (a moderator is also a member).
 * 
 * @author Denis Conan, J. Paul Gibson (translation to English)
 */
public class Participation {
	/**
	 * the user.
	 */
	private final User user;
	/**
	 * the social network.
	 */
	private final SocialNetwork socialNetwork;
	/**
	 * the qualification of the participation.
	 */
	private ParticipationRole qualification;
	/**
	 * the time of the last message read.
	 */
	private Instant instantLastMessageRead;
	/**
	 * the new message notification strategy.
	 */
	private NewMessageNotificationStrategy strategy;

	/**
	 * constructs a participation. In this constructor, the pseudo is taken from
	 * the user's pseudo.
	 * 
	 * @param user          the user.
	 * @param socialNetwork the social network.
	 * @param moderator     {@code true} if this is a participation as a moderator.
	 * @param strategy      the new message notification strategy.
	 */
	public Participation(final User user, final SocialNetwork socialNetwork, final boolean moderator,
			final NewMessageNotificationStrategy strategy) {
		if (user == null) {
			throw new IllegalArgumentException("user cannot be null");
		}
		if (socialNetwork == null) {
			throw new IllegalArgumentException("social network cannot be null");
		}
		if (strategy == null) {
			throw new IllegalArgumentException("strategy cannot be null");
		}
		this.user = user;
		this.socialNetwork = socialNetwork;
		this.qualification = (moderator) ? ParticipationRole.MODERATOR : ParticipationRole.MEMBER;
		this.strategy = strategy;
		user.addParticipation(this);
		this.instantLastMessageRead = Instant.now();
		assert invariant();
	}

	/**
	 * checks the class invariant.
	 * 
	 * @return if the invariant is respected.
	 */
	public boolean invariant() {
		return user != null && socialNetwork != null && qualification != null && instantLastMessageRead != null
				&& strategy != null;
	}

	/**
	 * gets the time of the last message read.
	 * 
	 * @return the time of the last message read.
	 */
	public Instant getInstantLastMessageRead() {
		return instantLastMessageRead;
	}

	/**
	 * update the last message read time.
	 */
	public void updateInstantLastMessageRead() {
		instantLastMessageRead = Instant.now();
		assert invariant();
	}

	/**
	 * gets the user.
	 * 
	 * @return the user.
	 */
	public User getUser() {
		return user;
	}

	/**
	 * gets the social network.
	 * 
	 * @return the social network.
	 */
	public SocialNetwork getSocialNetwork() {
		return socialNetwork;
	}

	/**
	 * obtains the role (qualification) of participation.
	 * 
	 * @return the role (qualification).
	 */
	public ParticipationRole getRole() {
		return qualification;
	}

	/**
	 * gets the new message notification strategy (policy).
	 * 
	 * @return the strategy.
	 */
	public NewMessageNotificationStrategy getStrategy() {
		return strategy;
	}

	@Override
	public int hashCode() {
		return Objects.hash(socialNetwork, getUser());
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Participation)) {
			return false;
		}
		Participation other = (Participation) obj;
		return Objects.equals(socialNetwork, other.socialNetwork) && Objects.equals(getUser(), other.getUser());
	}

	@Override
	public String toString() {
		return "Participation [user=" + user + ", socialNetwork=" + socialNetwork + ", qualification=" + qualification
				+ ", instantLastMessageRead=" + UtilSerialiser.formatInstantIntoString(instantLastMessageRead)
				+ ", strategy=" + strategy + "]";
	}
}
