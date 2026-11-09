package layer.rest;

import java.io.IOException;
import java.net.URI;

import minisocs.MiniSocsAPI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class RestServer {
    private final HttpServer server;

    public RestServer(final String baseUri, final MiniSocsAPI facade) {
        AbstractResource.setApplicationFacade(facade);
        var serverInfo = new ResourceConfig(ReadyResource.class, UserResource.class,
                SocialNetworkResource.class, ParticipationResource.class, AnomalyDetectionResource.class);
        
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUri), serverInfo, false);
    }
    
    public void startRestServer() throws IOException {server.start();}
    public void shutRestServerDown() {
        server.shutdownNow();
    }
}