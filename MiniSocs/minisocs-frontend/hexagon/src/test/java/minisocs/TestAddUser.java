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
import org.junit.jupiter.params.provider.ValueSource;

import minisocs.api.constants.NewMessageNotificationStrategy;
import minisocs.api.exceptions.UnfeasibleOperation;
import minisocs.api.port.driving.forrequestingminisocs.commands.AddUserCommand;

/**
 * The validation test class for the
 * {@link MiniSocs#addUser( String, String, String, String, NotificationConsumer, NewMessageNotificationStrategy)}
 * 
 * @author Denis Conan, J. Paul Gibson (translation to English)
 */
class TestAddUser {
	private MiniSocsAPI miniSocs;
	private String pseudo;
	private String name;
	private String firstName;
	private String email;

	@BeforeEach
	void setUp() {
		miniSocs = new MiniSocs(new NotificationManager());
		pseudo = "user1";
		name = "name1";
		firstName = "firstName1";
		email = "bon@courriel.fr";
	}

	@AfterEach
	void tearDown() {
		miniSocs = null;
		pseudo = null;
		name = null;
		firstName = null;
		email = null;
	}

	@ParameterizedTest
	@NullAndEmptySource
	void addUserTest1(String input) {
		Assertions.assertThrows(UnfeasibleOperation.class, () -> miniSocs
				.addUser(new AddUserCommand(input, name, firstName, email, NewMessageNotificationStrategy.IMMEDIATE)));
	}

	@ParameterizedTest
	@NullAndEmptySource
	void addUserTest2(String input) {
		Assertions.assertThrows(UnfeasibleOperation.class, () -> miniSocs.addUser(
				new AddUserCommand(pseudo, input, firstName, email, NewMessageNotificationStrategy.IMMEDIATE)));
	}

	@ParameterizedTest
	@NullAndEmptySource
	void addUserTest3(String input) {
		Assertions.assertThrows(UnfeasibleOperation.class, () -> miniSocs
				.addUser(new AddUserCommand(pseudo, name, input, email, NewMessageNotificationStrategy.IMMEDIATE)));
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { "mauvaiseadressecourriel" })
	void addUserTest4(String input) {
		Assertions.assertThrows(UnfeasibleOperation.class, () -> miniSocs
				.addUser(new AddUserCommand(pseudo, name, firstName, input, NewMessageNotificationStrategy.IMMEDIATE)));
	}

	@Test
	void addUserTest4then4() throws Exception {
		Assertions.assertTrue(miniSocs.listUsers().isEmpty());
		miniSocs.addUser(new AddUserCommand(pseudo, name, firstName, email, NewMessageNotificationStrategy.IMMEDIATE));
		Assertions.assertFalse(miniSocs.listUsers().isEmpty());
		Assertions.assertEquals(1, miniSocs.listUsers().size());
		Assertions.assertTrue(miniSocs.listUsers().get(0).pseudo().contains(pseudo));
		Assertions.assertTrue(miniSocs.listUsers().get(0).name().contains(name));
		Assertions.assertTrue(miniSocs.listUsers().get(0).firstName().contains(firstName));
		Assertions.assertTrue(miniSocs.listUsers().get(0).email().contains(email));
		Assertions.assertThrows(UnfeasibleOperation.class, () -> miniSocs
				.addUser(new AddUserCommand(pseudo, name, firstName, email, NewMessageNotificationStrategy.IMMEDIATE)));
	}
}
