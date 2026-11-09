package layer.sse;

import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import minisocs.api.constants.NewMessageNotificationStrategy;
import minisocs.api.port.driven.fornotifying.ForRegistering;
import minisocs.api.port.driven.fornotifying.Notification;
import minisocs.api.port.driven.fornotifying.NotificationConsumer;

public class SseAdapter implements ForRegistering {
    private final HttpServer http_server;

    public SseAdapter(final String baseUri) {
        var config = new ResourceConfig(SseReadyResource.class, NotificationResource.class);
        http_server = GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUri), config, false);
    }
    public void startSseServer() throws IOException {
        http_server.start();
    }

    public void shutSseServerDown() {
        http_server.shutdownNow();
    }

    @Override
    public void addUserConsumerObjectForModerationRequests(final String userPseudo,
            final NotificationConsumer consumer) {
    }

    @Override
    public void addUserConsumerObjectForNewMessages(final String userPseudo,
            final NewMessageNotificationStrategy strategy, final NotificationConsumer consumer) {
    }

    @Override
    public void createSocialNetwork(final String snName, final String user,
            final NewMessageNotificationStrategy strategy) {
    }

    @Override
    public void addMemberToSocialNetwork(final String snName, final String userPseudo,
            final NewMessageNotificationStrategy strategy) {
    }

    @Override
    public void notify(final String snName, final Notification notification) {
        var resource = NotificationResource.getNotificationResource();
        if (resource == null) {
            return;
        }
        resource.notify(snName, notification);
    }
}
