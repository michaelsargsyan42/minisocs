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
import minisocs.api.port.driving.forrequestingminisocs.commands.AddUserCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.CreateSocialNetworkCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.DeactivateUserAccountCommand;

/**
 * The validation test class for the
 * {@link MiniSocs#createSocialNetwork( String, String)}
 * 
 * @author Denis Conan, J. Paul Gibson (translation to English)
 */
class TestCreateSocialNetwork {
	private MiniSocsAPI miniSocs;
	private String pseudo;
	private String name;
	private String firstName;
	private String email;
	private String nameSN;

	@BeforeEach
	void setUp() throws UnfeasibleOperation {
		miniSocs = new MiniSocs(new NotificationManager());
		pseudo = "user1";
		name = "name1";
		firstName = "firstName1";
		email = "bon@courriel.fr";
		nameSN = "csc5002";
		miniSocs.addUser(new AddUserCommand(pseudo, name, firstName, email, NewMessageNotificationStrategy.IMMEDIATE));
		var consumer = new FlowApiNotificationConsumer("moderator for moderation requests");
		miniSocs.addUserConsumerObjectForNewMessages(pseudo, consumer);
		consumer = new FlowApiNotificationConsumer("moderator for new messages");
		miniSocs.addUserConsumerObjectForModerationRequests(pseudo, consumer);
		miniSocs.addUserConsumerObjectForNewMessages(pseudo, consumer);
	}

	@AfterEach
	void tearDown() {
		miniSocs = null;
		pseudo = null;
		name = null;
		firstName = null;
		email = null;
		nameSN = null;
	}

	@ParameterizedTest
	@NullAndEmptySource
	void createSocialNetworkTest1(String input) {
		Assertions.assertThrows(UnfeasibleOperation.class,
				() -> miniSocs.createSocialNetwork(new CreateSocialNetworkCommand(input, nameSN)));
	}

	@ParameterizedTest
	@NullAndEmptySource
	void createSocialNetworkTest2Case1(String input) {
		Assertions.assertThrows(UnfeasibleOperation.class,
				() -> miniSocs.createSocialNetwork(new CreateSocialNetworkCommand(pseudo, input)));
	}

	@Test
	void createSocialNetworkTest3() {
		Assertions.assertThrows(UnfeasibleOperation.class,
				() -> miniSocs.createSocialNetwork(new CreateSocialNetworkCommand("badpseudo", nameSN)));
	}

	@Test
	void createSocialNetworkTest4() throws Exception {
		miniSocs.deactivateUserAccount(new DeactivateUserAccountCommand(pseudo));
		Assertions.assertThrows(UnfeasibleOperation.class,
				() -> miniSocs.createSocialNetwork(new CreateSocialNetworkCommand(pseudo, nameSN)));
	}

	@Test
	void createSocialNetworkTest6Then5() throws Exception {
		Assertions.assertTrue(miniSocs.listSocialNetworks().isEmpty());
		miniSocs.createSocialNetwork(new CreateSocialNetworkCommand(pseudo, nameSN));
		Assertions.assertFalse(miniSocs.listSocialNetworks().isEmpty());
		Assertions.assertEquals(1, miniSocs.listSocialNetworks().size());
		Assertions.assertTrue(miniSocs.listSocialNetworks().get(0).name().contains(nameSN));
		Assertions.assertThrows(UnfeasibleOperation.class,
				() -> miniSocs.createSocialNetwork(new CreateSocialNetworkCommand(pseudo, nameSN)));
	}
}
