package org.javatraining.integration.gitlab.api.interfaces;

import org.javatraining.integration.gitlab.api.model.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;

/**
 * The project name is cms.
 * Created by sergey on 02.06.15 at 15:32.
 * For more information you should send mail to codedealerb@gmail.com
 */
@Path("/api/v3")
public interface GitLabAPIClient {

    @GET
    @Path("/users")
    @Produces("application/json")
    Collection<GitLabUser> getAllUsers(@QueryParam("private_token") String privateToken,
                                       @QueryParam("sudo") String sudo);

    @GET
    @Path("/users")
    @Produces("application/json")
    List<GitLabUser> getUser(@QueryParam("private_token") String privateToken,
                             @QueryParam("sudo") String sudo,
                             @QueryParam("search") String email);

    @POST
    @Path("/users")
    @Consumes("application/json")
    Response.Status createUser(@QueryParam("private_token") String privateToken,
                               @QueryParam("sudo") String sudo,
                               GitLabUser userProperties);

    @PUT
    @Path("/users")
    @Consumes("application/json")
    Response.Status updateUser(@QueryParam("private_token") String privateToken,
                               @QueryParam("sudo") String sudo,
                               GitLabUser userProperties);

    @DELETE
    @Path("/users/{id}")
    Response.Status removeUser(@QueryParam("private_token") String privateToken,
                               @QueryParam("sudo") String sudo,
                               @PathParam("id") Long id);

    @POST
    @Path("/session")
    @Consumes("application/json")
    @Produces("application/json")
    GitLabSession getSession(GitLabSessionParameters parameters, @QueryParam("sudo") String sudo);

    @POST
    @Path("/projects/user/{id}")
    @Consumes("application/json")
    Response.Status createProject(@QueryParam("private_token") String privateToken,
                                  @QueryParam("sudo") String sudo,
                                  @PathParam("id") Long id,
                                  GitLabProject projectProperties);

    @GET
    @Path("/projects/all")
    @Produces("application/json")
    Collection<GitLabProject> getAllProjects(@QueryParam("private_token") String privateToken,
                                             @QueryParam("sudo") String sudo);


    @POST
    @Path("/projects/{proj_id}/members")
    @Consumes("application/json")
    Response.Status addProjectTeamMember(@QueryParam("private_token") String privateToken,
                                         @QueryParam("sudo") String sudo,
                                         GitLabProjectMember projectMemberToAdd,
                                         @PathParam("proj_id") Integer projectId);

    @DELETE
    @Path("/projects/{proj_id}/members/{user_id}")
    Response.Status removeProjectTeamMember(@QueryParam("private_token") String privateToken,
                                            @QueryParam("sudo") String sudo,
                                            @PathParam("proj_id") Integer projectId,
                                            @PathParam("user_id") Long userId);
}
