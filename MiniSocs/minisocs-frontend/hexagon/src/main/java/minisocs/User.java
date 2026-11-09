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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.validator.routines.EmailValidator;

import minisocs.api.constants.NewMessageNotificationStrategy;

/**
 * This class implements the concept of a system user, not to be confused with
 * the concept of a participant, implicit in a social network.
 * 
 * @author Denis Conan, J. Paul Gibson (translation to English)
 */
public class User {
	/**
	 * the user's pseudo.
	 */
	private String pseudo;
	/**
	 * the user's name.
	 */
	private String name;
	/**
	 * the user's first name.
	 */
	private String firstName;
	/**
	 * the user's email address.
	 */
	private String email;
	/**
	 * user account status.
	 */
	private AccountState accountState;
	/**
	 * the notification strategy.
	 */
	private NewMessageNotificationStrategy strategy;
	/**
	 * participations in social networks.
	 */
	private List<Participation> participations;

	/**
	 * constructs a user.
	 * 
	 * @param pseudo the pseudo.
	 * @param name      the name
	 * @param firstName the first name.
	 * @param email     the user's email address.
	 * @param strategy  the notification strategy.
	 */
	public User(final String pseudo, final String name, final String firstName, final String email,
			final NewMessageNotificationStrategy strategy) {
		super();
		if (pseudo == null || pseudo.isBlank()) {
			throw new IllegalArgumentException("pseudo cannot be null or empty");
		}
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("name cannot be null or empty");
		}
		if (firstName == null || firstName.isBlank()) {
			throw new IllegalArgumentException("first name cannot be null or empty");
		}
		if (!EmailValidator.getInstance().isValid(email)) {
			throw new IllegalArgumentException("email does not comply with the RFC822 standard");
		}
		if (strategy == null) {
			throw new IllegalArgumentException("strategy cannot be null");
		}
		this.pseudo = pseudo;
		this.name = name;
		this.firstName = firstName;
		this.email = email;
		this.accountState = AccountState.ACTIVE;
		this.strategy = strategy;
		this.participations = new ArrayList<>();
		assert invariant();
	}

	/**
	 * checks the class invariant.
	 * 
	 * @return if the invariant is respected.
	 */
	public boolean invariant() {
		return pseudo != null && !pseudo.isBlank() && name != null && !name.isBlank() && firstName != null
				&& !firstName.isBlank() && EmailValidator.getInstance().isValid(email) && strategy != null
				&& accountState != null && participations != null;
	}

	/**
	 * gets the pseudo.
	 * 
	 * @return the value.
	 */
	public String getPseudo() {
		return pseudo;
	}

	/**
	 * gets the name.
	 * 
	 * @return the value.
	 */
	public String getName() {
		return name;
	}

	/**
	 * gets the first name.
	 * 
	 * @return the value.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * gets the email.
	 * 
	 * @return the value.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * gets the strategy.
	 * 
	 * @return the value.
	 */
	public NewMessageNotificationStrategy getStrategy() {
		return strategy;
	}

	/**
	 * gets the account state.
	 * 
	 * @return the value.
	 */
	public AccountState getAccountState() {
		return accountState;
	}

	/**
	 * Makes the user's account inactive. The operation is idempotent. The operation
	 * is refused if the account is not active.
	 */
	public void deactivateAccount() {
		if (accountState.equals(AccountState.DISABLED)) {
			return;
		}
		if (!accountState.equals(AccountState.ACTIVE)) {
			throw new IllegalStateException("the account is not active");
		}
		accountState = AccountState.DISABLED;
		assert invariant();
	}

	/**
	 * locks the user's account. The operation is idempotent.
	 */
	public void blockAccount() {
		accountState = AccountState.BLOCKED;
		assert invariant();
	}

	/**
	 * adds a participation.
	 * 
	 * @param p the participation to add.
	 */
	public void addParticipation(final Participation p) {
		if (!accountState.equals(AccountState.ACTIVE)) {
			throw new IllegalStateException("the account is not active");
		}
		if (p == null) {
			throw new IllegalArgumentException("participation cannot be null");
		}
		participations.add(p);
		assert invariant();
	}

	@Override
	public int hashCode() {
		return Objects.hash(pseudo);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof User)) {
			return false;
		}
		User other = (User) obj;
		return Objects.equals(pseudo, other.pseudo);
	}

	@Override
	public String toString() {
		return "Utilisateur [pseudo=" + pseudo + ", name=" + name + ", firstname=" + firstName + ", email="
				+ email + ", accountState=" + accountState + "]";
	}
}
