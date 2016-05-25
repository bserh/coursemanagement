package org.javatraining.ws.service;

import flexjson.JSONException;
import org.javatraining.auth.Auth;
import org.javatraining.config.AuthRole;
import org.javatraining.config.Config;
import org.javatraining.entity.enums.PersonRole;
import org.javatraining.model.CourseWithStatusVO;
import org.javatraining.model.PersonDescriptionVO;
import org.javatraining.model.PersonVO;
import org.javatraining.service.PersonService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.ValidationException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by asudak on 5/29/15.
 */
@Stateless
@Path("person")
public class PersonWebService extends AbstractWebService<PersonVO> {
    private static final String PARAM_DESCRIPTION = "field";
    private static final String PARAM_DESCRIPTION_ALL = "all";
    private static final String PARAM_DESCRIPTION_PHONE = "phone";
    @EJB
    private PersonService personService;

    public PersonWebService() {
        super(PersonVO.class);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{person_id}")
    @Auth(roles = {AuthRole.TEACHER, AuthRole.STUDENT})
    public Response getPerson(@HeaderParam(Config.REQUEST_HEADER_ID) long clientId, @PathParam("person_id") long personId) {
        Response.ResponseBuilder r = null;
        PersonVO client = personService.getById(clientId);

        if (client == null)
            return Response.status(Response.Status.FORBIDDEN).build();
        if (client.getPersonRole() != PersonRole.TEACHER)
            if (client.getId() != personId)
                r = Response.status(Response.Status.FORBIDDEN);

        if (r == null) {
            PersonVO person = personService.getById(personId);

            if (person == null)
                r = Response.noContent();
            else
                r = Response.ok(serialize(person));
        }

        return r.build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Auth(roles = {AuthRole.TEACHER})
    public Response getPersonsByRole(@QueryParam("role") String role) {
        Response.ResponseBuilder r;
        try {
            PersonRole personRole = PersonRole.valueOf(role.toUpperCase());
            List<PersonVO> personsByRole = personService.getPersonsByRole(personRole);
            r = Response.ok(personsByRole);
        } catch (IllegalArgumentException e) {
            r = Response.noContent();
        } catch (ValidationException e) {
            r = Response.status(422); //422 Unprocessable Entity
        }
        return r.build();
    }

    @GET
    @Produces("application/json")
    @Path("{person_id}/description")
    public Response getPersonDescription(@PathParam("person_id") long personId,
                                         @QueryParam(value = PARAM_DESCRIPTION) @DefaultValue(PARAM_DESCRIPTION_ALL) String field) {
        Response.ResponseBuilder r;
        try {
            PersonDescriptionVO personDescriptionVO;
            switch (field) {
                case PARAM_DESCRIPTION_ALL:
                    personDescriptionVO = personService.getPersonDescription(personId);
                    break;
                case PARAM_DESCRIPTION_PHONE:
                    personDescriptionVO = personService.getPersonPhone(personId);
                    break;
                default:
                    personDescriptionVO = personService.getPersonDescription(personId);
                    break;
            }
            r = Response.ok(personDescriptionVO);
        } catch (IllegalArgumentException e) {
            r = Response.noContent();
        } catch (ValidationException e) {
            r = Response.status(422); //422 Unprocessable Entity
        }
        return r.build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPerson(@Context UriInfo uriInfo, PersonVO person) {
        Response.ResponseBuilder r;
        try {
            personService.save(person);
            String personUri = uriInfo.getRequestUri().toString() + "/" + person.getId();
            r = Response.created(new URI(personUri)).entity(person);
        } catch (JSONException e) {
            r = Response.status(Response.Status.NOT_ACCEPTABLE);
        } catch (URISyntaxException e) {
            //this shouldn't happen
            r = Response.serverError();
        } catch (ValidationException e) {
            r = Response.status(422); //422 Unprocessable Entity
        }

        return r.build();
    }

    @POST
    @Consumes("application/json")
    @Path("description")
    public Response createPersonDescription(PersonDescriptionVO person) {
        return personService.savePersonDescription(person) ? Response.ok().build() : Response.status(422).build();
    }

    @PUT
    @Path("{person_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Auth(roles = {AuthRole.TEACHER, AuthRole.STUDENT})
    public Response updatePerson(@HeaderParam(Config.REQUEST_HEADER_ID) long clientId, @PathParam("person_id") long personId, @QueryParam("person_json") String personJson) {
        Response.ResponseBuilder r = null;
        PersonVO client = personService.getById(clientId);

        if (client == null)
            return Response.status(Response.Status.FORBIDDEN).build();

        if (client.getPersonRole() != PersonRole.TEACHER)
            if (client.getId() != personId)
                r = Response.status(Response.Status.FORBIDDEN);

        if (r == null) {
            PersonVO person = deserialize(personJson);
            personService.update(person);
            r = Response.ok();
        }

        return r.build();
    }

    @PUT
    @Path("{person_id}/description")
    @Consumes("application/json")
    public Response updatePersonDescription(@PathParam("person_id") long personId, PersonDescriptionVO personDescriptionVO) {
        PersonDescriptionVO personDesc = personService.getPersonDescription(personId);

        if (personDesc == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        personDesc = personService.updatePersonDescription(personDescriptionVO);

        return personDesc != null ? Response.ok().build() : Response.status(422).build();
    }

    @PUT
    @Path("{person_id}/phone")
    @Consumes("application/json")
    public Response updatePersonPhone(@PathParam("person_id") long personId, PersonDescriptionVO personDescriptionVO) {
        if (personDescriptionVO == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        personDescriptionVO.setId(personId);
        PersonDescriptionVO updatedPersonVO = personService.updatePersonPhone(personDescriptionVO);
        return updatedPersonVO != null ? Response.ok().build() : Response.status(422).build();
    }


    @DELETE
    @Path("{person_id}")
    @Auth(roles = {AuthRole.TEACHER, AuthRole.STUDENT})
    public Response deletePerson(@HeaderParam(Config.REQUEST_HEADER_ID) long clientId, @PathParam("person_id") long personId) {
        Response.ResponseBuilder r = null;
        PersonVO client = personService.getById(clientId);

        if (client == null)
            return Response.status(Response.Status.FORBIDDEN).build();

        if (client.getPersonRole() != PersonRole.TEACHER)
            if (client.getId() != personId)
                r = Response.status(Response.Status.FORBIDDEN);

        if (r == null) {

            try {
                PersonVO person = new PersonVO();
                person.setId(personId);
                personService.remove(person);
                r = Response.ok();
            } catch (Exception e) {
                r = Response.noContent();
            }
        }

        return r.build();
    }

    @DELETE
    @Path("{person_id}/description")
    public Response deletePersonDescription(@PathParam("person_id") long personId) {
        PersonDescriptionVO personDescriptionVO = personService.getPersonDescription(personId);

        if (personDescriptionVO == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        personService.removePersonDescription(personId);
        return Response.ok().build();
    }

    @GET
    @Path("{person_id}/course")
    @Produces(MediaType.APPLICATION_JSON)
    @Auth(roles = {AuthRole.TEACHER, AuthRole.STUDENT})
    public Response getCoursesForPerson(@PathParam("person_id") long personId) {
        List<CourseWithStatusVO> courses = personService.getPersonCoursesWithStatuses(new PersonVO(personId));
        return Response.ok(courses).build();
    }
}
