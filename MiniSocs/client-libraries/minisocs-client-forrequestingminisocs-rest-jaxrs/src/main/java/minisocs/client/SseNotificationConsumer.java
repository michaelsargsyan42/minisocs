package minisocs.client;

import java.util.function.Consumer;

import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.sse.InboundSseEvent;
import jakarta.ws.rs.sse.SseEventSource;
import minisocs.api.port.driven.fornotifying.Notification;
import minisocs.api.port.driven.fornotifying.NotificationConsumer;
import minisocs.common.UtilSerialiser;

// client side of see, reused milestone 3 code

public class SseNotificationConsumer implements NotificationConsumer, AutoCloseable {
    private final String name;
    private final Consumer<Notification> onEvent;
    private final Consumer<Throwable> onError;
    private final Runnable onComplete;
    private final SseEventSource source;

    public SseNotificationConsumer(final String name, final WebTarget endpoint,
            final Consumer<Notification> onEvent, final Consumer<Throwable> onError, final Runnable onComplete) {
        this.name = name;
        this.onEvent = onEvent;
        this.onError = onError;
        this.onComplete = onComplete;
        source = SseEventSource.target(endpoint).build();
        source.register(this::decode, this::onError, this::onComplete);
        source.open();
    }

    private void decode(final InboundSseEvent inbound) {
        try {
            var json = inbound.readData(String.class);
            if ("error".equals(inbound.getName())) {
                onError(new IllegalStateException("server refused registration: " + json));
                return;
            }
            onEvent(UtilSerialiser.readValue(json, Notification.class));
        } catch (Exception problem) {
            onError(problem);
        }
    }

    @Override
    public void onEvent(final Notification notification) {
        onEvent.accept(notification);
    }

    @Override
    public void onError(final Throwable throwable) {
        onError.accept(throwable);
    }

    @Override
    public void onComplete() {
        onComplete.run();
    }

    public boolean isOpen() {
        return source.isOpen();
    }

    @Override
    public void close() {
        source.close();
    }

    @Override
    public String toString() {
        return "SseNotificationConsumer [" + name + "]";
    }
}
