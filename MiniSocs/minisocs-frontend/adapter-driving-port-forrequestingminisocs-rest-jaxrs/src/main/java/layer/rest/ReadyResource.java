package layer.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class ReadyResource extends AbstractResource {
    @GET
    @Path("isready")
    @Produces(MediaType.TEXT_PLAIN)
    public Response isReady() {
        return Response.ok("isServerReady").build();
    }
}