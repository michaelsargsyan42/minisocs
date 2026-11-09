package layer.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import minisocs.api.port.driving.forrequestingminisocs.commands.AddUserCommand;
import minisocs.api.port.driving.forrequestingminisocs.commands.DeactivateUserAccountCommand;
import minisocs.common.UtilSerialiser;

@Path("/user")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.TEXT_PLAIN)
public class UserResource extends AbstractResource {
    @POST
    @Path("/add")
    public Response addUser(final String commandContent) {
        try {
            var commandForAddingAUser = UtilSerialiser.readValue(commandContent, AddUserCommand.class);
            accessApplicationFacade().addUser(commandForAddingAUser);

            return Response.status(Status.OK).build();
        }
        catch (Exception problem) {
            return Response.status(Status.BAD_REQUEST).entity(String.valueOf(problem.getMessage())).build();
        }
    }

    @GET
    @Path("/list")
    public Response listUsers() {
        try {
            var usersList = accessApplicationFacade().listUsers();
            return Response.status(Status.OK).entity(UtilSerialiser.writeValueAsString(usersList)).build();
        } catch (JsonProcessingException problem) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(String.valueOf(problem.getMessage())).build();
        }
    }

    @PUT
    @Path("/deactivate")
    public Response deactivateUserAccount(final String commandContent) {
        try {
            var commandForDeactivatingTheUser = UtilSerialiser.readValue(commandContent, DeactivateUserAccountCommand.class);
            accessApplicationFacade().deactivateUserAccount(commandForDeactivatingTheUser);
            return Response.status(Status.OK).build();
        }
        catch (Exception problem) {
            return Response.status(Status.BAD_REQUEST).entity(String.valueOf(problem.getMessage())).build();
        }
    }

}