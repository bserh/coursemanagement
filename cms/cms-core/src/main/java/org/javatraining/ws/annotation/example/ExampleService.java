package org.javatraining.ws.annotation.example;

import org.javatraining.auth.Auth;
import org.javatraining.config.AuthRole;
import org.javatraining.model.PersonVO;
import org.javatraining.service.PersonService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by olga on 05.06.15.
 */
@Path("example")
public class ExampleService {
    @Inject
    private PersonService personService;

    @POST
    @Path("/person")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPerson(PersonVO person) {
        personService.save(person);
        return Response.ok(person, MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    public Response getExample() {
        return Response.ok().build();
    }

    @GET
    @Path("/teacher/{id}")
    @Auth(roles = {AuthRole.TEACHER})
    public Response getTeacherInfo() {
        return Response.ok().build();
    }

    @GET
    @Path("/student/{id}")
    @Auth(roles = {AuthRole.STUDENT})
    public Response getStudentInfo() {
        return Response.ok().build();
    }
}
