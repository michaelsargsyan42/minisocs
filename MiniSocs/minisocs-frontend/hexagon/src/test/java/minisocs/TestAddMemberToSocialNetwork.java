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

/**
 * The validation test class for the
 * {@link MiniSocs#addMemberToSocialNetwork(String, String, String)}
 * 
 * @author Denis Conan, J. Paul Gibson (translation to English)
 */
class TestAddMemberToSocialNetwork {
	private MiniSocsAPI miniSocs;
	private String pseudoM;
	private String nameM;
	private String firstNameM;
	private String emailM;
	private String nameSN;
	private String pseudoN;
	private String nameN;
	private String firsNameN;
	private String emailN;

	@BeforeEach
	void setUp() throws UnfeasibleOperation {
		miniSocs = new MiniSocs(new NotificationManager());
		pseudoM = "user1";
		nameM = "name1";
		firstNameM = "firsName1";
		emailM = "bon@courriel.fr";
		nameSN = "csc5002";
		miniSocs.addUser(
				new AddUserCommand(pseudoM, nameM, firstNameM, emailM, NewMessageNotificationStrategy.IMMEDIATE));
		var consumer = new FlowApiNotificationConsumer("moderator for moderation requests");
		miniSocs.addUserConsumerObjectForNewMessages(pseudoM, consumer);
		consumer = new FlowApiNotificationConsumer("moderator for new messages");
		miniSocs.addUserConsumerObjectForModerationRequests(pseudoM, consumer);
		miniSocs.createSocialNetwork(new CreateSocialNetworkCommand(pseudoM, nameSN));
		pseudoN = "user2";
		nameN = "name2";
		firsNameN = "firstName2";
		emailN = "autrebon@courriel.fr";
		miniSocs.addUser(
				new AddUserCommand(pseudoN, nameN, firsNameN, emailN, NewMessageNotificationStrategy.IMMEDIATE));
		consumer = new FlowApiNotificationConsumer(pseudoN);
		miniSocs.addUserConsumerObjectForNewMessages(pseudoN, consumer);
		miniSocs.addUser(new AddUserCommand("not a moderator", "otherName", "otherFirstName", emailN,
				NewMessageNotificationStrategy.IMMEDIATE));
		consumer = new FlowApiNotificationConsumer("not a moderator");
		miniSocs.addUserConsumerObjectForNewMessages("not a moderator", consumer);
	}

	@AfterEach
	void tearDown() {
		miniSocs = null;
		pseudoM = null;
		nameM = null;
		firstNameM = null;
		emailM = null;
		nameSN = null;
		pseudoN = null;
		nameN = null;
		firsNameN = null;
		emailN = null;
	}

	@ParameterizedTest
	@NullAndEmptySource
	void addMemberToSocialNetworkTest1(String input) {
		Assertions.assertThrows(UnfeasibleOperation.class,
				() -> miniSocs.addMemberToSocialNetwork(new AddMemberToSocialNetworkCommand(input, nameSN, pseudoN)));
	}

	@ParameterizedTest
	@NullAndEmptySource
	void addMemberToSocialNetworkTest2(String input) {
		Assertions.assertThrows(UnfeasibleOperation.class,
				() -> miniSocs.addMemberToSocialNetwork(new AddMemberToSocialNetworkCommand(pseudoM, input, pseudoN)));
	}

	@ParameterizedTest
	@NullAndEmptySource
	void addMemberToSocialNetworkTest3Jeu1(String input) {
		Assertions.assertThrows(UnfeasibleOperation.class,
				() -> miniSocs.addMemberToSocialNetwork(new AddMemberToSocialNetworkCommand(pseudoM, nameSN, input)));
	}

	@Test
	void addMemberToSocialNetworkTest4() {
		Assertions.assertThrows(UnfeasibleOperation.class, () -> miniSocs
				.addMemberToSocialNetwork(new AddMemberToSocialNetworkCommand("bad pseudoM", nameSN, pseudoN)));
	}

	@Test
	void addMemberToSocialNetworkTest5() throws Exception {
		miniSocs.deactivateUserAccount(new DeactivateUserAccountCommand(pseudoM));
		Assertions.assertThrows(UnfeasibleOperation.class,
				() -> miniSocs.addMemberToSocialNetwork(new AddMemberToSocialNetworkCommand(pseudoM, nameSN, pseudoN)));
	}

	@Test
	void addMemberToSocialNetworkTest12Then6() throws Exception {
		miniSocs.addMemberToSocialNetwork(new AddMemberToSocialNetworkCommand(pseudoM, nameSN, "not a moderator"));
		Assertions.assertThrows(UnfeasibleOperation.class, () -> miniSocs
				.addMemberToSocialNetwork(new AddMemberToSocialNetworkCommand("not a moderateur", nameSN, pseudoN)));
	}

	@Test
	void addMemberToSocialNetworkTest7() {
		Assertions.assertThrows(UnfeasibleOperation.class, () -> miniSocs
				.addMemberToSocialNetwork(new AddMemberToSocialNetworkCommand(pseudoM, "bad nameSN", pseudoN)));
	}

	@Test
	void addMemberToSocialNetworkTest8() throws Exception {
		miniSocs.closeSocialNetwork(new CloseSocialNetworkCommand(pseudoM, nameSN));
		Assertions.assertThrows(UnfeasibleOperation.class,
				() -> miniSocs.addMemberToSocialNetwork(new AddMemberToSocialNetworkCommand(pseudoM, nameSN, pseudoN)));
	}

	@Test
	void addMemberToSocialNetworkTest9() {
		Assertions.assertThrows(UnfeasibleOperation.class, () -> miniSocs
				.addMemberToSocialNetwork(new AddMemberToSocialNetworkCommand("pseudoM", nameSN, "bad pseudoN")));
	}

	@Test
	void addMemberToSocialNetworkTest10() throws Exception {
		miniSocs.deactivateUserAccount(new DeactivateUserAccountCommand(pseudoN));
		Assertions.assertThrows(UnfeasibleOperation.class,
				() -> miniSocs.addMemberToSocialNetwork(new AddMemberToSocialNetworkCommand(pseudoM, nameSN, pseudoN)));
	}

	@Test
	void addMemberToSocialNetworkTest12Then11() throws Exception {
		Assertions.assertEquals(1,
				miniSocs.listModeratorsSocialNetwork(new ListModeratorsSocialNetworkCommand(nameSN)).size());
		Assertions.assertEquals(1,
				miniSocs.listMembersSocialNetwork(new ListMembersSocialNetworkCommand(nameSN)).size());
		miniSocs.addMemberToSocialNetwork(new AddMemberToSocialNetworkCommand(pseudoM, nameSN, pseudoN));
		Assertions.assertEquals(1,
				miniSocs.listModeratorsSocialNetwork(new ListModeratorsSocialNetworkCommand(nameSN)).size());
		Assertions.assertEquals(2,
				miniSocs.listMembersSocialNetwork(new ListMembersSocialNetworkCommand(nameSN)).size());
		Assertions.assertThrows(UnfeasibleOperation.class,
				() -> miniSocs.addMemberToSocialNetwork(new AddMemberToSocialNetworkCommand(pseudoM, nameSN, pseudoN)));
	}
}
