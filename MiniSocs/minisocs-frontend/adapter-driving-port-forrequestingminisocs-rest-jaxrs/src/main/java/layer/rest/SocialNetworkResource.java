package layer.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import minisocs.api.exceptions.UnfeasibleOperation;
import minisocs.api.port.driving.forrequestingminisocs.commands.*;
import minisocs.common.UtilSerialiser;

@Path("/socialnetwork")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.TEXT_PLAIN)
public class SocialNetworkResource extends AbstractResource {
    @POST
    @Path("/create")
    public Response createSocialNetwork(final String commandContent) {
        try {
            var commandForCreatingASocialNetwork = UtilSerialiser.readValue(commandContent, CreateSocialNetworkCommand.class);
            accessApplicationFacade().createSocialNetwork(commandForCreatingASocialNetwork);

            return Response.status(Status.OK).build();
        }

        catch (Exception problem) {
            return Response.status(Status.BAD_REQUEST).entity(String.valueOf(problem.getMessage())).build();
        }
    }

    @GET
    @Path("/list")
    public Response listSocialNetworks() {
        try {
            var socialNetworksList = accessApplicationFacade().listSocialNetworks();
            return Response.status(Status.OK).entity(UtilSerialiser.writeValueAsString(socialNetworksList)).build();
        } catch (JsonProcessingException problem) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(String.valueOf(problem.getMessage())).build();
        }
    }

    @PUT
    @Path("/close")
    public Response closeSocialNetwork(final String commandContent) {
        try {
            var commandForClosingSocialNetwork = UtilSerialiser.readValue(commandContent, CloseSocialNetworkCommand.class);
            accessApplicationFacade().closeSocialNetwork(commandForClosingSocialNetwork);
            return Response.status(Status.OK).build();
        }
        catch (Exception problem) {
            return Response.status(Status.BAD_REQUEST).entity(String.valueOf(problem.getMessage())).build();
        }
    }

    @GET
    @Path("/listMembers")
    public Response listMembersOfSocialNetwork(@QueryParam("nameSN") final String nameSN) {
        try {
            var members = accessApplicationFacade().listMembersSocialNetwork(new ListMembersSocialNetworkCommand(nameSN));
            return Response.status(Status.OK).entity(UtilSerialiser.writeValueAsString(members)).build();
        } catch (UnfeasibleOperation problem) {
            return Response.status(Status.BAD_REQUEST).entity(String.valueOf(problem.getMessage())).build();
        } catch (JsonProcessingException problem) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(String.valueOf(problem.getMessage())).build();
        }
    }

    @GET
    @Path("/listModerators")
    public Response listModeratorsSocialNetwork(@QueryParam("nameSN") final String socialNetworkName) {
        try {
            var moderators = accessApplicationFacade().listModeratorsSocialNetwork(new ListModeratorsSocialNetworkCommand(socialNetworkName));
            return Response.status(Status.OK).entity(UtilSerialiser.writeValueAsString(moderators)).build();
        } catch (UnfeasibleOperation problem) {
            return Response.status(Status.BAD_REQUEST).entity(String.valueOf(problem.getMessage())).build();
        } catch (JsonProcessingException problem) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(String.valueOf(problem.getMessage())).build();
        }
    }

    @PUT
    @Path("/addMember")
    public Response addMemberToSocialNetwork(final String commandContent) {
        try {
            var commandForAddingAMember = UtilSerialiser.readValue(commandContent, AddMemberToSocialNetworkCommand.class);
            accessApplicationFacade().addMemberToSocialNetwork(commandForAddingAMember);

            return Response.status(Status.OK).build();
        }
        catch (Exception problem) {
            return Response.status(Status.BAD_REQUEST).entity(String.valueOf(problem.getMessage())).build();
        }
    }
}