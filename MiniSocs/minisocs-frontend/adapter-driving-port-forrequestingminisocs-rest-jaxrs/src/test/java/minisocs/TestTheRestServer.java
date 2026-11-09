package minisocs;
import java.time.Duration;
import java.time.Instant;
import layer.rest.RestServer;
import minisocs.api.port.driving.forrequestingminisocs.commands.ReadLatestMessagesCommand;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import minisocs.api.constants.NewMessageNotificationStrategy;

import minisocs.common.UtilSerialiser;
import java.util.List;

import minisocs.api.port.driving.forrequestingminisocs.commands.AddUserCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.CreateSocialNetworkCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.PostMessageCommand;

class TestTheRestServer {

    private RestServer serverRestEntity;

    private Client clientEntity;

    private WebTarget restService;

    @BeforeEach
    void setUp() throws Exception {
        var facade = new MiniSocs(null);
        serverRestEntity = new RestServer("http://localhost:8083/MiniSocs", facade);
        serverRestEntity.startRestServer();
        clientEntity = ClientBuilder.newClient();
        restService = clientEntity.target("http://localhost:8083/MiniSocs");

        Awaitility.await().atMost(Duration.ofSeconds(5)).until(() -> {
            try {
                var response = restService.path("/isready").request().get();
                return response.getStatus() == Response.Status.OK.getStatusCode() && "isServerReady".equals(response.readEntity(String.class));
            }
            catch (Exception problem) {
                return false;
            }
        });
    }

    @AfterEach
    void tearDown() {
        if (clientEntity != null) {clientEntity.close();}
        if (serverRestEntity != null) {serverRestEntity.shutRestServerDown();}
    }


    @Test
    void ScenarioModeratorCreatesNetworkAndPosts() throws Exception {
        checkIfEqualElseError(post("/user/add", moderator()));
        checkIfEqualElseError(post("/socialnetwork/create", new CreateSocialNetworkCommand("moderatorBro", "SuperCoolNetwork")));
        checkIfEqualElseError(put("/participation/post",
                new PostMessageCommand("moderatorBro", "SuperCoolNetwork", "Sup World", Instant.now())));
    }

    @Test
    void ScenarioNonMemberCannotPost() throws Exception {
        checkIfEqualElseError(post("/user/add", moderator()));
        checkIfEqualElseError(post("/socialnetwork/create", new CreateSocialNetworkCommand("moderatorBro", "notSoCoolNetwork")));
        checkIfEqualElseError(post("/user/add", new AddUserCommand("fakeModerator", "fakeSargsyan", "fakeMikayel",
                "mikayel_sargsyan_not_fake@gmail.com", NewMessageNotificationStrategy.IMMEDIATE)));

        Assertions.assertEquals(1, listSocialNetworks().size());

        var response = put("/participation/post",
                new PostMessageCommand("fakeModerator", "notSoCoolNetwork", "trust me i am a moderator", Instant.now()));
        Assertions.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    private static AddUserCommand moderator() {
        return new AddUserCommand("moderatorBro", "Sargsyan", "Mikayel", "mikayel.sargsyan@telecom-sudparis.eu",
                NewMessageNotificationStrategy.IMMEDIATE);
    }

    private Response post(final String path, final Object command) throws Exception {
        return restService.path(path).request().post(Entity.text(UtilSerialiser.writeValueAsString(command)));
    }

    private Response put(final String path, final Object command) throws Exception {
        return restService.path(path).request().put(Entity.text(UtilSerialiser.writeValueAsString(command)));
    }

    private static void checkIfEqualElseError(final Response response) {
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(),
                () -> "server's response: " + response.readEntity(String.class));
    }

    private List<?> listSocialNetworks() throws Exception {
        var response = restService.path("/socialnetwork/list").request().get();
        checkIfEqualElseError(response);
        return UtilSerialiser.listOfSocialNetworkReturnValuesFromJson(response.readEntity(String.class));
    }

    private int readLatest(final String pseudo, final String network) throws Exception {
        var response = put("/participation/readLatestMessages", new ReadLatestMessagesCommand(pseudo, network));
        checkIfEqualElseError(response);
        return UtilSerialiser.newMessagesFromJson(response.readEntity(String.class)).newMessages().length;
    }

    private List<?> listModerators(final String network) throws Exception {
        var response = restService.path("/socialnetwork/listModerators").queryParam("nameSN", network).request().get();
        checkIfEqualElseError(response);
        return UtilSerialiser.listOfUserReturnValuesFromJson(response.readEntity(String.class));
    }

    private List<?> listUsers() throws Exception {
        var response = restService.path("/user/list").request().get();
        checkIfEqualElseError(response);
        return UtilSerialiser.listOfUserReturnValuesFromJson(response.readEntity(String.class));
    }

    private List<?> listMembers(final String network) throws Exception {
        var response = restService.path("/socialnetwork/listMembers").queryParam("nameSN", network).request().get();
        checkIfEqualElseError(response);
        return UtilSerialiser.listOfUserReturnValuesFromJson(response.readEntity(String.class));
    }
}