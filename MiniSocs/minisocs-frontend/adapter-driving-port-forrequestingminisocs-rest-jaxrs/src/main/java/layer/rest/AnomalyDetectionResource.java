package layer.rest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import minisocs.api.port.driving.forrequestingminisocs.commands.AddAnomalyDetectionCommand;
import minisocs.common.UtilSerialiser;

/**
 * entry point for rest for add an anomaly detection type use case
 */
@Path("/anomalydetection")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.TEXT_PLAIN)
public class AnomalyDetectionResource extends AbstractResource {
    @POST
    @Path("/add")
    public Response addAnAnomalyDetectionType(final String commandContent) {
        try {
            var command = UtilSerialiser.readValue(commandContent, AddAnomalyDetectionCommand.class);
            accessApplicationFacade().addAnAnomalyDetectionType(command);
            return Response.status(Status.OK).build();
        } catch (Exception problem) {
            return Response.status(Status.BAD_REQUEST).entity(String.valueOf(problem.getMessage())).build();
        }
    }
}
