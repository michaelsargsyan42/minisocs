package minisocs.client;

import java.time.Instant;
import java.util.function.Consumer;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import minisocs.api.constants.NewMessageNotificationStrategy;
import minisocs.api.exceptions.UnfeasibleOperation;
import minisocs.api.port.driven.fordetecting.api.command.AnomalyType;
import minisocs.api.port.driven.fordetecting.api.command.DetectionTarget;
import minisocs.api.port.driven.fornotifying.Notification;
import minisocs.api.port.driving.forrequestingminisocs.commands.AddAnomalyDetectionCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.AddMemberToSocialNetworkCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.AddUserCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.CloseSocialNetworkCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.CreateSocialNetworkCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.DeactivateUserAccountCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.ModerateMessageCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.PostMessageCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.ReadLatestMessagesCommand;
import minisocs.common.UtilSerialiser;

public class MiniSocsClient implements AutoCloseable {
    private final Client restClient;
    private final Client sseClient;
    private final WebTarget rest;
    private final WebTarget sse;

    public MiniSocsClient(final String restBaseUri, final String sseBaseUri) {
        restClient = ClientBuilder.newClient();
        sseClient = ClientBuilder.newClient();
        rest = restClient.target(restBaseUri);
        sse = sseClient.target(sseBaseUri);
    }

    public void addUser(final String pseudo, final String name, final String firstName, final String email,
            final NewMessageNotificationStrategy strategy) throws UnfeasibleOperation {
        send("POST", "/user/add", new AddUserCommand(pseudo, name, firstName, email, strategy));
    }

    public String listUsers() throws UnfeasibleOperation {
        return check(rest.path("/user/list").request().get());
    }

    public void deactivateUserAccount(final String pseudo) throws UnfeasibleOperation {
        send("PUT", "/user/deactivate", new DeactivateUserAccountCommand(pseudo));
    }

    public void createSocialNetwork(final String pseudo, final String nameSN) throws UnfeasibleOperation {
        send("POST", "/socialnetwork/create", new CreateSocialNetworkCommand(pseudo, nameSN));
        check(sse.path("/notification/create").request().post(Entity.text(nameSN)));
    }

    public String listSocialNetworks() throws UnfeasibleOperation {
        return check(rest.path("/socialnetwork/list").request().get());
    }

    public void closeSocialNetwork(final String pseudo, final String nameSN) throws UnfeasibleOperation {
        send("PUT", "/socialnetwork/close", new CloseSocialNetworkCommand(pseudo, nameSN));
    }

    public void addMember(final String pseudoModerator, final String nameSN, final String pseudoNewMember)
            throws UnfeasibleOperation {
        send("PUT", "/socialnetwork/addMember",
                new AddMemberToSocialNetworkCommand(pseudoModerator, nameSN, pseudoNewMember));
    }

    public String listMembers(final String nameSN) throws UnfeasibleOperation {
        return check(rest.path("/socialnetwork/listMembers").queryParam("nameSN", nameSN).request().get());
    }

    public String listModerators(final String nameSN) throws UnfeasibleOperation {
        return check(rest.path("/socialnetwork/listModerators").queryParam("nameSN", nameSN).request().get());
    }

    public Instant postMessage(final String pseudo, final String nameSN, final String message)
            throws UnfeasibleOperation {
        var instant = Instant.now();
        send("PUT", "/participation/post", new PostMessageCommand(pseudo, nameSN, message, instant));
        return instant;
    }

    public void moderateMessage(final String pseudo, final String nameSN, final Instant instant,
            final boolean decision) throws UnfeasibleOperation {
        send("PUT", "/participation/moderate", new ModerateMessageCommand(pseudo, nameSN, instant, decision));
    }

    public String readLatestMessages(final String pseudo, final String nameSN) throws UnfeasibleOperation {
        return send("PUT", "/participation/readLatestMessages", new ReadLatestMessagesCommand(pseudo, nameSN));
    }

    public void addAnAnomalyDetectionType(final String nameSN, final String pseudo, final AnomalyType anomalyType)
            throws UnfeasibleOperation {
        send("POST", "/anomalydetection/add",
                new AddAnomalyDetectionCommand(new DetectionTarget(nameSN, pseudo), anomalyType));
    }

    // sse
    public SseNotificationConsumer receiveNewMessages(final String nameSN,
            final NewMessageNotificationStrategy strategy, final Consumer<Notification> onNotification) {
        var endpoint = sse.path("/notification/register").queryParam("snname", nameSN)
                .queryParam("strategy", strategy.name());
        return new SseNotificationConsumer(nameSN, endpoint, onNotification, Throwable::printStackTrace, () -> { });
    }

    public SseNotificationConsumer receiveModerationRequests(final String nameSN,
            final Consumer<Notification> onNotification) {
        var endpoint = sse.path("/notification/registermoderator").queryParam("snname", nameSN);
        return new SseNotificationConsumer(nameSN, endpoint, onNotification, Throwable::printStackTrace, () -> { });
    }

    @Override
    public void close() {
        restClient.close();
        sseClient.close();
    }

    private String send(final String method, final String path, final Object command) throws UnfeasibleOperation {
        String json;
        try {
            json = UtilSerialiser.writeValueAsString(command);
        } catch (Exception e) {
            throw new UnfeasibleOperation(e.getMessage());
        }
        return check(rest.path(path).request().method(method, Entity.text(json)));
    }
    private static String check(final Response response) throws UnfeasibleOperation {
        var status = response.getStatus();
        var body = response.hasEntity() ? response.readEntity(String.class) : "";
        response.close();
        if (status >= 300) {
            throw new UnfeasibleOperation(body);
        }
        return body;
    }
}
