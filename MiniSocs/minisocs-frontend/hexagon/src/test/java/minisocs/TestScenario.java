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
along with the course csc5002.  If not, see <http://www.gnu.org/licenses/>.

Initial developer(s): Denis Conan
Contributor(s): J. Paul Gibson (translation to English)
*/

// CHECKSTYLE:OFF
package minisocs;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Level;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import minisocs.api.constants.NewMessageNotificationStrategy;
import minisocs.api.exceptions.UnfeasibleOperation;
import minisocs.api.port.driving.forrequestingminisocs.commands.AddMemberToSocialNetworkCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.AddUserCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.CreateSocialNetworkCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.ListMembersSocialNetworkCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.ListModeratorsSocialNetworkCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.ModerateMessageCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.PostMessageCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.ReadLatestMessagesCommand;
import minisocs.api.port.driving.forrequestingminisocs.returnvalues.NewMessages;
import minisocs.common.Log;

/**
 * The validation test class for the {@link MiniSocs} example scenario
 * 
 * @author Denis Conan, J. Paul Gibson (translation to English)
 */
class TestScenario {
	private MiniSocsAPI miniSocs;

	@BeforeEach
	void setUp() {
		Awaitility.setDefaultPollDelay(50, TimeUnit.MILLISECONDS);
		Awaitility.setDefaultPollInterval(100, TimeUnit.MILLISECONDS);
		Awaitility.setDefaultTimeout(5, TimeUnit.SECONDS);
		FlowApiNotificationConsumer.resetNbNotificationsReceived();
		Log.setLevel(Log.TEST, Level.DEBUG);
		Log.setLevel(Log.MINISOCS, Level.DEBUG);
	}

	@Test
	void scenario1() throws UnfeasibleOperation, InterruptedException {
		miniSocs = new MiniSocs(new NotificationManager());
		// moderator user
		miniSocs.addUser(new AddUserCommand("moderator", "nameModerator", "firstNameModerator", "moderator@courriel.fr",
				NewMessageNotificationStrategy.IMMEDIATE));
		var consumer = new FlowApiNotificationConsumer("moderator for moderation requests");
		miniSocs.addUserConsumerObjectForNewMessages("moderator", consumer);
		consumer = new FlowApiNotificationConsumer("moderator for new messages");
		miniSocs.addUserConsumerObjectForModerationRequests("moderator", consumer);
		// social network csc5002 with moderator and immediate notification
		miniSocs.createSocialNetwork(new CreateSocialNetworkCommand("moderator", "csc5002"));
		// member user
		miniSocs.addUser(new AddUserCommand("member", "nameMember", "firstNameMember", "member@courriel.fr",
				NewMessageNotificationStrategy.IMMEDIATE));
		consumer = new FlowApiNotificationConsumer("member");
		miniSocs.addUserConsumerObjectForNewMessages("member", consumer);
		// member in csc5002 with immediate notification
		miniSocs.addMemberToSocialNetwork(new AddMemberToSocialNetworkCommand("moderator", "csc5002", "member"));
		// user other member
		miniSocs.addUser(new AddUserCommand("otherMember", "nameOtherMember", "firstNameOtherMember",
				"otherMember@courriel.fr", NewMessageNotificationStrategy.DAILY));
		consumer = new FlowApiNotificationConsumer("otherMember");
		miniSocs.addUserConsumerObjectForNewMessages("otherMember", consumer);
		// other member in csc5002 with daily notification
		miniSocs.addMemberToSocialNetwork(new AddMemberToSocialNetworkCommand("moderator", "csc5002", "otherMember"));
		// user last member
		miniSocs.addUser(new AddUserCommand("lastMember", "nameLastMember", "firstNameLastMember",
				"lastMember@courriel.fr", NewMessageNotificationStrategy.NO_NOTIFICATION));
		consumer = new FlowApiNotificationConsumer("lastMember");
		miniSocs.addUserConsumerObjectForNewMessages("lastMember", consumer);
		// last member in csc5002 with no notifications
		miniSocs.addMemberToSocialNetwork(new AddMemberToSocialNetworkCommand("moderator", "csc5002", "lastMember"));
		// non-member user
		miniSocs.addUser(new AddUserCommand("nonMember", "nameNonMember", "firstNameNonMember", "nonMember@courriel.fr",
				NewMessageNotificationStrategy.IMMEDIATE));
		consumer = new FlowApiNotificationConsumer("nonMember");
		miniSocs.addUserConsumerObjectForNewMessages("nonMember", consumer);
		// 5 users, 1 social network with 1 moderator and 4 members
		Assertions.assertEquals(5, miniSocs.listUsers().size());
		Assertions.assertEquals(1, miniSocs.listSocialNetworks().size());
		Assertions.assertEquals(1,
				miniSocs.listModeratorsSocialNetwork(new ListModeratorsSocialNetworkCommand("csc5002")).size());
		Assertions.assertEquals(4,
				miniSocs.listMembersSocialNetwork(new ListMembersSocialNetworkCommand("csc5002")).size());
		// post messageModerator by moderator => acceptance => notification
		miniSocs.postMessage(new PostMessageCommand("moderator", "csc5002", "messageModerator", Instant.now()));
		Awaitility.await().until(() -> FlowApiNotificationConsumer.getNbNotificationsReceived() == 2);
		// post messageMember by member => moderation request
		Instant instant = Instant.now();
		miniSocs.postMessage(new PostMessageCommand("member", "csc5002", "messageMember", instant));
		Awaitility.await().until(() -> FlowApiNotificationConsumer.getNbNotificationsReceived() == 3);
		// moderation with acceptance => notification
		miniSocs.moderateMessage(new ModerateMessageCommand("moderator", "csc5002", instant, true));
		Awaitility.await().until(() -> FlowApiNotificationConsumer.getNbNotificationsReceived() == 5);
		// post messageMember2 by member => moderation request
		instant = Instant.now();
		miniSocs.postMessage(new PostMessageCommand("member", "csc5002", "messageMember2", instant));
		Awaitility.await().until(() -> FlowApiNotificationConsumer.getNbNotificationsReceived() == 6);
		// moderation with non-acceptance => message not visible
		miniSocs.moderateMessage(new ModerateMessageCommand("moderator", "csc5002", instant, false));
		Awaitility.await().until(() -> FlowApiNotificationConsumer.getNbNotificationsReceived() == 6);
		// post messageNonMember by non-member => no moderation request
		Assertions.assertThrows(UnfeasibleOperation.class, () -> miniSocs
				.postMessage(new PostMessageCommand("nonMember", "csc5002", "messageNonMember", Instant.now())));
		Awaitility.await().until(() -> FlowApiNotificationConsumer.getNbNotificationsReceived() == 6);
		NewMessages msgs = miniSocs.readLatestMessages(new ReadLatestMessagesCommand("moderator", "csc5002"));
		Assertions.assertEquals(0, msgs.newMessages().length);
		msgs = miniSocs.readLatestMessages(new ReadLatestMessagesCommand("member", "csc5002"));
		Assertions.assertEquals(0, msgs.newMessages().length);
		msgs = miniSocs.readLatestMessages(new ReadLatestMessagesCommand("otherMember", "csc5002"));
		Assertions.assertEquals(2, msgs.newMessages().length);
		msgs = miniSocs.readLatestMessages(new ReadLatestMessagesCommand("lastMember", "csc5002"));
		Assertions.assertEquals(2, msgs.newMessages().length);
		// post otherMessageModerator by the moderator => acceptance => notification
		miniSocs.postMessage(new PostMessageCommand("moderator", "csc5002", "otherMessageModerator", Instant.now()));
		Awaitility.await().until(() -> FlowApiNotificationConsumer.getNbNotificationsReceived() == 8);
		miniSocs.emulateEndOfDay();
		Awaitility.await().until(() -> FlowApiNotificationConsumer.getNbNotificationsReceived() == 9);
	}
}
