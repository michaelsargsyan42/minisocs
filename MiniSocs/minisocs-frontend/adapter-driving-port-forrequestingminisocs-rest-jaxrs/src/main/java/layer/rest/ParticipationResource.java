package layer.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import minisocs.api.port.driving.forrequestingminisocs.commands.ModerateMessageCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.PostMessageCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.ReadLatestMessagesCommand;
import minisocs.common.UtilSerialiser;

@Path("/participation")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.TEXT_PLAIN)
public class ParticipationResource extends AbstractResource {
    @PUT
    @Path("/post")
    public Response postMessage(final String commandContent) {
        try {
            var commandForPostingAMessage = UtilSerialiser.readValue(commandContent, PostMessageCommand.class);
            accessApplicationFacade().postMessage(commandForPostingAMessage);

            return Response.status(Status.OK).build();
        }

        catch (Exception problem) {
            return Response.status(Status.BAD_REQUEST).entity(String.valueOf(problem.getMessage())).build();
        }
    }

    @PUT
    @Path("/moderate")
    public Response moderateMessage(final String commandContent) {
        try {
            var commandForModerating = UtilSerialiser.readValue(commandContent, ModerateMessageCommand.class);
            accessApplicationFacade().moderateMessage(commandForModerating);

            return Response.status(Status.OK).build();
        }

        catch (Exception problem) {
            return Response.status(Status.BAD_REQUEST).entity(String.valueOf(problem.getMessage())).build();
        }
    }

    @PUT
    @Path("/readLatestMessages")
    public Response readLatestMessages(final String commandContent) {
        try {
            var commandLatestMessages = UtilSerialiser.readValue(commandContent, ReadLatestMessagesCommand.class);
            var messages = accessApplicationFacade().readLatestMessages(commandLatestMessages);

            return Response.status(Status.OK).entity(UtilSerialiser.writeValueAsString(messages)).build();
        }

        catch (Exception problem) {
            return Response.status(Status.BAD_REQUEST).entity(String.valueOf(problem.getMessage())).build();
        }
    }
}