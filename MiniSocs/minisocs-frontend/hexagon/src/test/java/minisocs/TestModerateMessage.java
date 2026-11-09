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

// CHECKSTYLE:OFF
package minisocs;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

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

/**
 * The validation test class for the
 * {@link MiniSocs#moderateMessage( String, String, Instant, boolean)}
 * 
 * @author Denis Conan, J. Paul Gibson (translation to English)
 */
class TestModerateMessage {
	private MiniSocsAPI miniSocs;
	private String pseudo;
	private String nameSN;
	private String message;
	private Instant instant;

	@BeforeEach
	void setUp() throws UnfeasibleOperation {
		miniSocs = new MiniSocs(new NotificationManager());
		pseudo = "moderator";
		miniSocs.addUser(new AddUserCommand(pseudo, "nameModerator", "firstNameModerator", "moderator@courriel.fr",
				NewMessageNotificationStrategy.IMMEDIATE));
		var consumer = new FlowApiNotificationConsumer("moderator for moderation requests");
		miniSocs.addUserConsumerObjectForNewMessages("moderator", consumer);
		consumer = new FlowApiNotificationConsumer("moderator for new messages");
		miniSocs.addUserConsumerObjectForModerationRequests("moderator", consumer);
		FlowApiNotificationConsumer.resetNbNotificationsReceived();
		nameSN = "csc5002";
		miniSocs.createSocialNetwork(new CreateSocialNetworkCommand(pseudo, nameSN));
		miniSocs.addUser(new AddUserCommand("member", "nameMember", "firstNameMember", "member@courriel.fr",
				NewMessageNotificationStrategy.IMMEDIATE));
		consumer = new FlowApiNotificationConsumer("member");
		miniSocs.addUserConsumerObjectForNewMessages("member", consumer);
		miniSocs.addMemberToSocialNetwork(new AddMemberToSocialNetworkCommand(pseudo, nameSN, "member"));
		miniSocs.addUser(new AddUserCommand("other", "othername", "otherfirstname", "othermember@courriel.fr",
				NewMessageNotificationStrategy.IMMEDIATE));
		consumer = new FlowApiNotificationConsumer("other");
		miniSocs.addUserConsumerObjectForNewMessages("other", consumer);
		message = "message1";
		instant = Instant.now();
		Awaitility.setDefaultPollDelay(50, TimeUnit.MILLISECONDS);
		Awaitility.setDefaultPollInterval(100, TimeUnit.MILLISECONDS);
		Awaitility.setDefaultTimeout(5, TimeUnit.SECONDS);
		miniSocs.postMessage(new PostMessageCommand("member", nameSN, message, instant));
		Awaitility.await().until(() -> FlowApiNotificationConsumer.getNbNotificationsReceived() == 1);
	}

	@AfterEach
	void tearDown() {
		miniSocs = null;
		pseudo = null;
		nameSN = null;
		message = null;
		instant = null;
	}

	@ParameterizedTest
	@NullAndEmptySource
	void moderateMessageTest1(String input) {
		Assertions.assertThrows(UnfeasibleOperation.class,
				() -> miniSocs.moderateMessage(new ModerateMessageCommand(input, nameSN, instant, true)));
	}

	@ParameterizedTest
	@NullAndEmptySource
	void moderateMessageTest2(String input) {
		Assertions.assertThrows(UnfeasibleOperation.class,
				() -> miniSocs.moderateMessage(new ModerateMessageCommand(pseudo, input, instant, true)));
	}

	@Test
	void moderateMessageTest3() {
		Assertions.assertThrows(UnfeasibleOperation.class,
				() -> miniSocs.moderateMessage(new ModerateMessageCommand(pseudo, nameSN, null, true)));
	}

	@Test
	void moderateMessageTest4() {
		Assertions.assertThrows(UnfeasibleOperation.class,
				() -> miniSocs.moderateMessage(new ModerateMessageCommand(pseudo, "badnameSN", instant, true)));
	}

	@Test
	void moderateMessageTest5() throws Exception {
		miniSocs.closeSocialNetwork(new CloseSocialNetworkCommand(pseudo, nameSN));
		Assertions.assertThrows(UnfeasibleOperation.class,
				() -> miniSocs.moderateMessage(new ModerateMessageCommand(pseudo, nameSN, instant, true)));
	}

	@Test
	void moderateMessageTest6() {
		Assertions.assertThrows(UnfeasibleOperation.class,
				() -> miniSocs.moderateMessage(new ModerateMessageCommand("badpseudo", nameSN, instant, true)));
	}

	@Test
	void moderateMessageTest7() throws Exception {
		miniSocs.deactivateUserAccount(new DeactivateUserAccountCommand(pseudo));
		Assertions.assertThrows(UnfeasibleOperation.class,
				() -> miniSocs.moderateMessage(new ModerateMessageCommand(pseudo, nameSN, instant, true)));
	}

	@Test
	void moderateMessageTest8() {
		Assertions.assertThrows(UnfeasibleOperation.class,
				() -> miniSocs.moderateMessage(new ModerateMessageCommand("member", nameSN, instant, true)));
	}

	@Test
	void moderateMessageTest9() {
		Assertions.assertThrows(UnfeasibleOperation.class, () -> miniSocs
				.moderateMessage(new ModerateMessageCommand(pseudo, nameSN, instant.plusSeconds(1), true)));
	}

	@Test
	void moderatMessageTest11Case1then10() throws Exception {
		Assertions.assertEquals(1,
				miniSocs.listModeratorsSocialNetwork(new ListModeratorsSocialNetworkCommand(nameSN)).size());
		Assertions.assertEquals(2,
				miniSocs.listMembersSocialNetwork(new ListMembersSocialNetworkCommand(nameSN)).size());
		FlowApiNotificationConsumer.resetNbNotificationsReceived();
		miniSocs.moderateMessage(new ModerateMessageCommand(pseudo, nameSN, instant, true));
		Awaitility.await().until(() -> FlowApiNotificationConsumer.getNbNotificationsReceived() == 2);
		Assertions.assertThrows(UnfeasibleOperation.class,
				() -> miniSocs.moderateMessage(new ModerateMessageCommand(pseudo, nameSN, instant, true)));
	}

	@Test
	void moderateMessageTest11Case2() throws Exception {
		Assertions.assertEquals(1,
				miniSocs.listModeratorsSocialNetwork(new ListModeratorsSocialNetworkCommand(nameSN)).size());
		Assertions.assertEquals(2,
				miniSocs.listMembersSocialNetwork(new ListMembersSocialNetworkCommand(nameSN)).size());
		FlowApiNotificationConsumer.resetNbNotificationsReceived();
		miniSocs.moderateMessage(new ModerateMessageCommand(pseudo, nameSN, instant, false));
		Assertions.assertEquals(0, FlowApiNotificationConsumer.getNbNotificationsReceived());
	}
}
