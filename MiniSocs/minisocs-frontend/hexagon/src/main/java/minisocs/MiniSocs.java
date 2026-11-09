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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import minisocs.api.port.driven.fornotifying.ForRegistering;
import minisocs.api.port.driven.fornotifying.Notification;
import minisocs.api.port.driven.fornotifying.NotificationConsumer;
import minisocs.api.port.driven.fornotifying.TypeNotification;
import org.apache.commons.validator.routines.EmailValidator;

import com.fasterxml.jackson.core.JsonProcessingException;

import minisocs.api.constants.NewMessageNotificationStrategy;
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

import minisocs.api.port.driven.fordetecting.api.ForDetecting;
import minisocs.api.port.driven.fordetecting.api.command.AddNewMessageAndDetectAnomalyCommand;
import minisocs.api.port.driven.fordetecting.api.command.MessageForDetection;
import minisocs.api.port.driving.forrequestingminisocs.commands.AddAnomalyDetectionCommand;
/**
 * This class is the facade of the software application.
 * 
 * @author Denis Conan, J. Paul Gibson (translation to English)
 */
public class MiniSocs implements MiniSocsAPI {
	/**
	 * the users.
	 */
	private final Map<String, User> users;
	/**
	 * the social networks.
	 */
	private final Map<String, SocialNetwork> socialNetworks;
	/**
	 * the adapter to register to the notification manager that is paired with the
	 * Façade. It can be null.
	 */
	private ForRegistering notificationManager;


	private final ForDetecting forDetecting;


	public MiniSocs(final ForRegistering notificationManager) {
		this(notificationManager, null);
	}

	public MiniSocs(final ForRegistering notificationManager, final ForDetecting forDetecting) {
		this.users = new HashMap<>();
		this.socialNetworks = new HashMap<>();
		this.notificationManager = notificationManager;
		this.forDetecting = forDetecting;
	}

	/**
	 * the facade invariant.
	 * 
	 * @return {@code true} if the invariant is respected.
	 */
	public boolean invariant() {
		return users != null && socialNetworks != null;
	}

	/**
	 * sets the notification manager. This must be done only once.
	 * 
	 * @param notificationManager the new reference.
	 */
	public void setNotificationManager(final ForRegistering notificationManager) {
		if (notificationManager == null) {
			throw new IllegalArgumentException("the notification manager cannot be null");
		}
		if (this.notificationManager != null) {
			throw new IllegalStateException("the notification manager has already been set");
		}
		this.notificationManager = notificationManager;
	}

	/**
	 * adds a user.
	 * 
	 * @param command the command.
	 * @throws UnfeasibleOperation in case of problem with the preconditions.
	 */
	public void addUser(final AddUserCommand command) throws UnfeasibleOperation {
		MINISOCS.info("{}", () -> command);
		if (command.pseudo() == null || command.pseudo().isBlank()) {
			throw new UnfeasibleOperation("pseudo cannot be null or empty");
		}
		if (command.name() == null || command.name().isBlank()) {
			throw new UnfeasibleOperation("name cannot be null or empty");
		}
		if (command.firstName() == null || command.firstName().isBlank()) {
			throw new UnfeasibleOperation("first name cannot be null or empty");
		}
		if (command.email() == null || command.email().isBlank()) {
			throw new UnfeasibleOperation("email cannot be null or empty");
		}
		if (!EmailValidator.getInstance().isValid(command.email())) {
			throw new UnfeasibleOperation("email does not comply with the RFC822 standard");
		}
		if (command.strategy() == null) {
			throw new UnfeasibleOperation("strategy cannot be null");
		}
		var u = users.get(command.pseudo());
		if (u != null) {
			throw new UnfeasibleOperation(command.pseudo() + "already a user");
		}
		MINISOCS.debug("{}", () -> "addUser: precondition fulfilled");
		users.put(command.pseudo(),
				new User(command.pseudo(), command.name(), command.firstName(), command.email(), command.strategy()));
		assert invariant();
		MINISOCS.info("{}", () -> "End of addUser");
	}

	/**
	 * adds a user consumer object to prepare the receipt of moderation request
	 * notifications.
	 * 
	 * @param userPseudo the pseudo of the user that is added to the social network.
	 * @param consumer   the object consumer provided by the user to prepare the
	 *                   receipt of notifications.
	 * @throws UnfeasibleOperation in case of problem with the preconditions.
	 */
	public void addUserConsumerObjectForModerationRequests(final String userPseudo, final NotificationConsumer consumer)
			throws UnfeasibleOperation {
		MINISOCS.info("{}", () -> "register user " + userPseudo + " to receive notifications");
		if (userPseudo == null || userPseudo.isBlank()) {
			throw new UnfeasibleOperation("userPseudo cannot be null or empty");
		}
		if (consumer == null) {
			throw new UnfeasibleOperation("consumer object cannot be null or empty");
		}
		if (notificationManager == null) {
			throw new IllegalStateException("there is not notification manager");
		}
		var user = users.get(userPseudo);
		if (user == null) {
			throw new UnfeasibleOperation("user does not exist");
		}
		notificationManager.addUserConsumerObjectForModerationRequests(userPseudo, consumer);
	}

	/**
	 * adds a user consumer object to prepare the receipt of new message
	 * notifications.
	 * 
	 * @param userPseudo the pseudo of the user that is added to the social network.
	 * @param consumer   the object consumer provided by the user to prepare the
	 *                   receipt of notifications.
	 * @throws UnfeasibleOperation in case of problem with the preconditions.
	 */
	public void addUserConsumerObjectForNewMessages(final String userPseudo, final NotificationConsumer consumer)
			throws UnfeasibleOperation {
		MINISOCS.info("{}", () -> "register user " + userPseudo + " to receive notifications");
		if (userPseudo == null || userPseudo.isBlank()) {
			throw new UnfeasibleOperation("userPseudo cannot be null or empty");
		}
		if (consumer == null) {
			throw new UnfeasibleOperation("consumer object cannot be null or empty");
		}
		if (notificationManager == null) {
			throw new IllegalStateException("there is not notification manager");
		}
		var user = users.get(userPseudo);
		if (user == null) {
			throw new UnfeasibleOperation("user does not exist");
		}
		notificationManager.addUserConsumerObjectForNewMessages(userPseudo, user.getStrategy(), consumer);
	}

	/**
	 * list the users.
	 * 
	 * @return the list of user return values.
	 */
	public List<UserReturnValue> listUsers() {
		MINISOCS.info("{}", () -> "list users");
		return users.values().stream().map(
				u -> new UserReturnValue(u.getPseudo(), u.getFirstName(), u.getName(), u.getEmail(), u.getStrategy()))
				.toList();
	}

	/**
	 * deactivate a user's account.
	 * 
	 * @param command the command.
	 * @throws UnfeasibleOperation in case of problems with the preconditions.
	 */
	public void deactivateUserAccount(final DeactivateUserAccountCommand command) throws UnfeasibleOperation {
		MINISOCS.info("{}", () -> command);
		if (command.pseudo() == null || command.pseudo().isBlank()) {
			throw new UnfeasibleOperation("pseudo cannot be null or empty");
		}
		var u = users.get(command.pseudo());
		if (u == null) {
			throw new UnfeasibleOperation("non-existent user with this pseudo (" + command.pseudo() + ")");
		}
		if (u.getAccountState().equals(AccountState.BLOCKED)) {
			throw new UnfeasibleOperation("the account is blocked");
		}
		u.deactivateAccount();
		assert invariant();
		MINISOCS.info("{}", () -> "End of deactivateUserAccount");
	}

	/**
	 * creates a social network.
	 * 
	 * @param command the command.
	 * @throws UnfeasibleOperation in case of problems with the preconditions.
	 */
	public void createSocialNetwork(final CreateSocialNetworkCommand command) throws UnfeasibleOperation {
		MINISOCS.info("{}", () -> command);
		if (command.pseudo() == null || command.pseudo().isBlank()) {
			throw new UnfeasibleOperation("pseudo cannot be null or empty");
		}
		if (command.nameSN() == null || command.nameSN().isBlank()) {
			throw new UnfeasibleOperation("name cannot be null or empty");
		}
		var u = users.get(command.pseudo());
		if (u == null) {
			throw new UnfeasibleOperation("non-existent user with this pseudo (" + command.pseudo() + ")");
		}
		if (!u.getAccountState().equals(AccountState.ACTIVE)) {
			throw new UnfeasibleOperation("the account of  " + command.pseudo() + " is not active");
		}
		SocialNetwork r = socialNetworks.get(command.nameSN());
		if (r != null) {
			throw new UnfeasibleOperation("a social network with this name already exists (" + command.nameSN() + ")");
		}
		MINISOCS.debug("{}", () -> "createSocialNetwork: precondition fulfilled");
		socialNetworks.put(command.nameSN(), new SocialNetwork(command.nameSN(), u));
		if (!u.getStrategy().equals(NewMessageNotificationStrategy.NO_NOTIFICATION) && notificationManager != null) {
			notificationManager.createSocialNetwork(command.nameSN(), u.getPseudo(), u.getStrategy());
		}
		assert invariant();
		MINISOCS.info("{}", () -> "End of createSocialNetwork");
	}

	/**
	 * list the social networks.
	 * 
	 * @return a list of social network return values.
	 */
	public List<SocialNetworkReturnValue> listSocialNetworks() {
		MINISOCS.info("{}", () -> "list social networks");
		return socialNetworks.values().stream().map(sn -> new SocialNetworkReturnValue(sn.getName())).toList();
	}

	/**
	 * list the moderators of a social network.
	 * 
	 * @param command the command
	 * @return the list of moderators, as user return values.
	 * @throws UnfeasibleOperation in case of problems with the preconditions.
	 */
	public List<UserReturnValue> listModeratorsSocialNetwork(final ListModeratorsSocialNetworkCommand command)
			throws UnfeasibleOperation {
		MINISOCS.info("{}", () -> command);
		if (command.nameSN() == null || command.nameSN().isBlank()) {
			throw new UnfeasibleOperation("social network name cannot be null or empty");
		}
		SocialNetwork sn = socialNetworks.get(command.nameSN());
		if (sn == null) {
			throw new UnfeasibleOperation("no social network with this name (" + command.nameSN() + ")");
		}
		MINISOCS.debug("{}", () -> "listModeratorsSocialNetwork: precondition fulfilled");
		return sn.listModerators().stream().map(
				u -> new UserReturnValue(u.getPseudo(), u.getFirstName(), u.getName(), u.getEmail(), u.getStrategy()))
				.toList();
	}

	/**
	 * closes a social network.
	 * 
	 * @param command the command.
	 * @throws UnfeasibleOperation in case of problems with the preconditions.
	 */
	public void closeSocialNetwork(final CloseSocialNetworkCommand command) throws UnfeasibleOperation {
		MINISOCS.info("{}", () -> command);
		if (command.pseudo() == null || command.pseudo().isBlank()) {
			throw new UnfeasibleOperation("pseudo cannot be null or empty");
		}
		if (command.nameSN() == null || command.nameSN().isBlank()) {
			throw new UnfeasibleOperation("nameSN cannot be null or empty");
		}
		var u = users.get(command.pseudo());
		if (u == null) {
			throw new UnfeasibleOperation(command.pseudo() + " does not exist with this pseudo");
		}
		if (!u.getAccountState().equals(AccountState.ACTIVE)) {
			throw new UnfeasibleOperation("the user account is not active");
		}
		SocialNetwork sn = socialNetworks.get(command.nameSN());
		if (sn == null) {
			throw new UnfeasibleOperation("no social network with this name (" + command.nameSN() + ")");
		}
		if (!sn.isModerator(u)) {
			throw new UnfeasibleOperation(command.pseudo() + " is not a moderator of the social network");
		}
		MINISOCS.debug("{}", () -> "closeSocialNetwork: precondition fulfilled");
		sn.close();
		assert invariant();
		MINISOCS.info("{}", () -> "End of closeSocialNetwork");
	}

	/**
	 * adds a member to a social network.
	 * 
	 * @param command the command.
	 * @throws UnfeasibleOperation in case of problems with the preconditions.
	 */
	public void addMemberToSocialNetwork(final AddMemberToSocialNetworkCommand command) throws UnfeasibleOperation {
		MINISOCS.info("{}", () -> command);
		if (command.pseudoModerator() == null || command.pseudoModerator().isBlank()) {
			throw new UnfeasibleOperation("moderator pseduo cannot be null or empty");
		}
		if (command.nameSN() == null || command.nameSN().isBlank()) {
			throw new UnfeasibleOperation("social network name cannot be null or empty");
		}
		if (command.pseudoNewMember() == null || command.pseudoNewMember().isBlank()) {
			throw new UnfeasibleOperation("new member's pseduo cannot be null or empty");
		}
		SocialNetwork sn = socialNetworks.get(command.nameSN());
		if (sn == null) {
			throw new UnfeasibleOperation("no social network with this name (" + command.nameSN() + ")");
		}
		if (!sn.getSocialNetworkState().equals(SocialNetworkState.OPEN)) {
			throw new UnfeasibleOperation("the social network is not open");
		}
		var moderateur = users.get(command.pseudoModerator());
		if (moderateur == null) {
			throw new UnfeasibleOperation("no moderator with this  pseudo (" + command.pseudoModerator() + ")");
		}
		var membre = users.get(command.pseudoNewMember());
		if (membre == null) {
			throw new UnfeasibleOperation("no user with this pseudo (" + command.pseudoNewMember() + ")");
		}
		if (!moderateur.getAccountState().equals(AccountState.ACTIVE)) {
			throw new UnfeasibleOperation("the moderator's account is not active");
		}
		if (!membre.getAccountState().equals(AccountState.ACTIVE)) {
			throw new UnfeasibleOperation("the new member's account is not active");
		}
		MINISOCS.debug("{}", () -> "addMemberToSocialNetwork: precondition fulfilled");
		sn.addMember(moderateur, membre, notificationManager);
		assert invariant();
		MINISOCS.info("{}", () -> "End of addMemberToSocialNetwork");
	}

	/**
	 * lists the members of a social network.
	 * 
	 * @param command the command.
	 * @return a list of members, as user return values.
	 * @throws UnfeasibleOperation in case of problem with the preconditions.
	 */
	public List<UserReturnValue> listMembersSocialNetwork(final ListMembersSocialNetworkCommand command)
			throws UnfeasibleOperation {
		MINISOCS.info("{}", () -> command);
		if (command.nameSN() == null || command.nameSN().isBlank()) {
			throw new UnfeasibleOperation("social network name cannot be null or empty");
		}
		SocialNetwork sn = socialNetworks.get(command.nameSN());
		if (sn == null) {
			throw new UnfeasibleOperation("no social network with this name (" + command.nameSN() + ")");
		}
		MINISOCS.debug("{}", () -> "listMembersSocialNetwork: precondition fulfilled");
		return sn.listMembers().stream().map(
				u -> new UserReturnValue(u.getPseudo(), u.getFirstName(), u.getName(), u.getEmail(), u.getStrategy()))
				.toList();
	}

	/**
	 * posts a message, meaning a user posts a message on a social network.
	 * 
	 * @param command the command.
	 * @throws UnfeasibleOperation in case of problems with the preconditions.
	 */
	public void postMessage(final PostMessageCommand command) throws UnfeasibleOperation {
		MINISOCS.info("{}", () -> command);
		if (command.pseudo() == null || command.pseudo().isBlank()) {
			throw new UnfeasibleOperation("member pseudo cannot be null or empty");
		}
		if (command.nameSocialNetwork() == null || command.nameSocialNetwork().isBlank()) {
			throw new UnfeasibleOperation("social network name cannot be null or empty");
		}
		if (command.message() == null || command.message().isBlank()) {
			throw new UnfeasibleOperation("message cannot be null or empty");
		}
		if (command.instant() == null) {
			throw new UnfeasibleOperation("instant cannot be null");
		}
		SocialNetwork sn = socialNetworks.get(command.nameSocialNetwork());
		if (sn == null) {
			throw new UnfeasibleOperation("no social network with this name (" + command.nameSocialNetwork() + ")");
		}
		if (!sn.getSocialNetworkState().equals(SocialNetworkState.OPEN)) {
			throw new UnfeasibleOperation("the social network is not open");
		}
		var member = users.get(command.pseudo());
		if (member == null) {
			throw new UnfeasibleOperation("no user with this pseudo (" + command.pseudo() + ")");
		}
		if (!member.getAccountState().equals(AccountState.ACTIVE)) {
			throw new UnfeasibleOperation("the user account is not active");
		}
		MINISOCS.debug("{}", () -> "postMessage: precondition fulfilled");
		try {
			sn.postMessage(member, command.message(), command.instant(), notificationManager);
		} catch (JsonProcessingException ex) {
			MINISOCS.warn("{}", () -> "serialisation problem: " + ex.getMessage());
			throw new UnfeasibleOperation("serialisation problem: " + ex.getMessage());
		}

		if (forDetecting != null) {
			try {
				forDetecting.addNewMessageAndPerformAnomalyDetection(new AddNewMessageAndDetectAnomalyCommand(
						new MessageForDetection(command.nameSocialNetwork(), command.pseudo(), command.instant(),
								command.message())));
			} catch (UnfeasibleOperation ex) {
				MINISOCS.warn("{}", () -> "anomaly detection not requested: " + ex.getMessage());
			}
		}

		assert invariant();
		MINISOCS.info("{}", () -> "End of postMessage");
	}

	/**
	 * moderates a message from a social network.
	 * 
	 * @param command the command.
	 * @throws UnfeasibleOperation in case of problems with the preconditions.
	 */
	public void moderateMessage(final ModerateMessageCommand command) throws UnfeasibleOperation {
		MINISOCS.info("{}", () -> command);
		if (command.pseudo() == null || command.pseudo().isBlank()) {
			throw new UnfeasibleOperation("member pseudo cannot be null or empty");
		}
		if (command.nameSocialNetwork() == null || command.nameSocialNetwork().isBlank()) {
			throw new UnfeasibleOperation("social network name cannot be null or empty");
		}
		if (command.instant() == null) {
			throw new UnfeasibleOperation("instant cannot be null");
		}
		SocialNetwork sn = socialNetworks.get(command.nameSocialNetwork());
		if (sn == null) {
			throw new UnfeasibleOperation("no social network with this name (" + command.nameSocialNetwork() + ")");
		}
		if (!sn.getSocialNetworkState().equals(SocialNetworkState.OPEN)) {
			throw new UnfeasibleOperation("the social network is not open");
		}
		var user = users.get(command.pseudo());
		if (user == null) {
			throw new UnfeasibleOperation("no user with this pseudo (" + command.pseudo() + ")");
		}
		if (!user.getAccountState().equals(AccountState.ACTIVE)) {
			throw new UnfeasibleOperation("the user account is not active");
		}
		MINISOCS.debug("{}", () -> "moderateMessage: precondition fulfilled");
		try {
			sn.moderateMessage(user, command.instant(), command.decision(), notificationManager);
		} catch (JsonProcessingException ex) {
			MINISOCS.warn("{}", () -> "serialisation problem: " + ex.getMessage());
			throw new UnfeasibleOperation("serialisation problem: " + ex.getMessage());
		}
		assert invariant();
		MINISOCS.info("{}", () -> "End of moderateMessage");
	}

	/**
	 * read the latest social media messages not yet read by the user.
	 * 
	 * @param command the command.
	 * @return the list of new unread messages.
	 * @throws UnfeasibleOperation in case of problems with the preconditions.
	 */
	public NewMessages readLatestMessages(final ReadLatestMessagesCommand command) throws UnfeasibleOperation {
		MINISOCS.info("{}", () -> command);
		if (command.pseudo() == null || command.pseudo().isBlank()) {
			throw new UnfeasibleOperation("member nickname cannot be null or empty");
		}
		if (command.nameSocialNetwork() == null || command.nameSocialNetwork().isBlank()) {
			throw new UnfeasibleOperation("social network name cannot be null or empty");
		}
		SocialNetwork sn = socialNetworks.get(command.nameSocialNetwork());
		if (sn == null) {
			throw new UnfeasibleOperation("no social network with this name (" + command.nameSocialNetwork() + ")");
		}
		if (!sn.getSocialNetworkState().equals(SocialNetworkState.OPEN)) {
			throw new UnfeasibleOperation("the social network is not open");
		}
		var member = users.get(command.pseudo());
		if (member == null) {
			throw new UnfeasibleOperation("no user with this nickname (" + command.pseudo() + ")");
		}
		if (!member.getAccountState().equals(AccountState.ACTIVE)) {
			throw new UnfeasibleOperation("the user account is not active");
		}
		MINISOCS.info("{}", () -> "End of readLatestMessages");
		return sn.readUnreadMessages((User) member);
	}


	public void addAnAnomalyDetectionType(final AddAnomalyDetectionCommand command) throws UnfeasibleOperation {
		MINISOCS.info("{}", () -> command);
		if (command == null || command.target() == null) {
			throw new UnfeasibleOperation("detection target cannot be null");
		}
		if (command.target().pseudo() == null || command.target().pseudo().isBlank()) {
			throw new UnfeasibleOperation("pseudo cannot be null or empty");
		}
		if (command.target().snName() == null || command.target().snName().isBlank()) {
			throw new UnfeasibleOperation("social network name cannot be null or empty");
		}
		if (command.anomalyType() == null) {
			throw new UnfeasibleOperation("anomaly type cannot be null");
		}
		if (users.get(command.target().pseudo()) == null) {
			throw new UnfeasibleOperation("no user with this pseudo (" + command.target().pseudo() + ")");
		}
		if (socialNetworks.get(command.target().snName()) == null) {
			throw new UnfeasibleOperation("no social network with this name (" + command.target().snName() + ")");
		}
		if (forDetecting == null) {
			throw new UnfeasibleOperation("anomaly detection is not available");
		}
		MINISOCS.debug("{}", () -> "addAnAnomalyDetectionType: precondition fulfilled");
		forDetecting.addAnAnomalyDetectionType(command);
		MINISOCS.info("{}", () -> "End of addAnAnomalyDetectionType");
	}


	/**
	 * emulates the end of the day.
	 */
	public void emulateEndOfDay() throws UnfeasibleOperation {
		MINISOCS.info("{}", () -> "Beginning of emulateEndOfDay");
		for (SocialNetwork sn : socialNetworks.values()) {
			try {
				sn.emulateEndOfDay(notificationManager);
			} catch (JsonProcessingException ex) {
				MINISOCS.warn("{}", () -> "serialisation problem: " + ex.getMessage());
				throw new UnfeasibleOperation("serialisation problem: " + ex.getMessage());
			}
		}
		assert invariant();
		MINISOCS.info("{}", () -> "End of emulateEndOfDay");
	}

	@Override
	public String toString() {
		return "MiniSocs [users=" + users + ", socialNetworks=" + socialNetworks + "]";
	}
}
