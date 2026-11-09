package layer.sse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;
import minisocs.api.constants.NewMessageNotificationStrategy;
import minisocs.api.port.driven.fornotifying.Notification;
import minisocs.common.UtilSerialiser;

@Path("/notification")
@Singleton
public class NotificationResource {
    private static NotificationResource instance;
    private static Sse sse;

    private final Map<String, SseBroadcaster> mods = new ConcurrentHashMap<>();
    private final Map<String, SseBroadcaster> immediate = new ConcurrentHashMap<>();
    private final Map<String, SseBroadcaster> daily = new ConcurrentHashMap<>();


    public NotificationResource(@Context final Sse sse) {
        NotificationResource.sse = sse;
    }

    @PostConstruct
    public void init() {
        NotificationResource.instance = this;
    }

    public static NotificationResource getNotificationResource() {
        return instance;
    }

    // create network broadcasters
    @POST
    @Path("create")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response createSocialNetwork(final String network_name) {
        if (network_name == null || network_name.isBlank()) {
            return Response.status(Status.BAD_REQUEST).entity("network name can't be empty").build();
        }

        daily.computeIfAbsent(network_name, name -> sse.newBroadcaster());

        mods.computeIfAbsent(network_name, name -> sse.newBroadcaster());

        immediate.computeIfAbsent(network_name, name -> sse.newBroadcaster());

        return Response.status(Status.OK).build();
    }

    // client registers for alerts
    @GET
    @Path("register")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void registerForNewMessages(@QueryParam("snname") final String snName,
            @QueryParam("strategy") final String strategy, @Context final SseEventSink eventSink) {
        NewMessageNotificationStrategy parsed;
        try {
            parsed = NewMessageNotificationStrategy.valueOf(strategy);
        } catch (IllegalArgumentException | NullPointerException problem) {
            refuse(eventSink, "strategy is not known: " + strategy);
            return;
        }

        switch (parsed) {
            case IMMEDIATE -> register(immediate, snName, eventSink);
            case DAILY -> register(daily, snName, eventSink);
            default -> refuse(eventSink, "strategy " + parsed + " got no notification");
        }
    }

    // mod joins to mod messages
    @GET
    @Path("registermoderator")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void registerForModerationRequests(@QueryParam("snname") final String social_network_name,
            @Context final SseEventSink eventSink) {
        register(mods, social_network_name, eventSink);
    }
    private void register(final Map<String, SseBroadcaster> broadcasters, final String social_network_name,
            final SseEventSink eventSink) {
        var broadcaster = social_network_name == null?null : broadcasters.get(social_network_name);

        if (broadcaster == null) {
            refuse(eventSink, "social network is unknown " + social_network_name);
            return;
        }
        broadcaster.register(eventSink);
    }
    private void refuse(final SseEventSink eventSink, final String reason) {
        eventSink.send(sse.newEventBuilder().name("error").data(String.class, reason).build());
        try {
            eventSink.close();
        } catch (IOException problem) {
            System.out.println("can't close connection: " + problem.getMessage());
        }
    }

    public void notify(final String snName, final Notification notification) {
        var broadcasters = switch (notification.type()) {
            case MODERATION_REQUEST -> mods;
            case NEW_MESSAGE -> immediate;
            case NEW_MESSAGES -> daily;
            default -> null;
        };
        var broadcaster = broadcasters == null ? null : broadcasters.get(snName);
        if (broadcaster == null) {
            return;
        }
        try {
            var event = sse.newEventBuilder()
                    .name(notification.type().name())
                    .mediaType(MediaType.TEXT_PLAIN_TYPE)
                    .data(String.class, UtilSerialiser.writeValueAsString(notification))
                    .build();
            broadcaster.broadcast(event);
        } catch (Exception problem) {
            System.out.println("can't broadcast " + problem.getMessage());
        }
    }
}
