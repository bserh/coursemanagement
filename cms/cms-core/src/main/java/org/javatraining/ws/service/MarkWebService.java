package org.javatraining.ws.service;

import flexjson.JSONException;
import org.javatraining.auth.Auth;
import org.javatraining.config.AuthRole;
import org.javatraining.model.CourseVO;
import org.javatraining.model.MarkVO;
import org.javatraining.model.PersonVO;
import org.javatraining.service.PersonService;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by asudak on 6/11/15.
 */
@Path("person")
public class MarkWebService extends AbstractWebService<MarkVO> {
    @EJB
    private PersonService personService;

    public MarkWebService() {
        super(MarkVO.class);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{person_id}/course/{course_id}/marks")
    @Auth(roles = {AuthRole.TEACHER, AuthRole.STUDENT})
    public Response getMarks(@PathParam("person_id") long personId, @PathParam("course_id") long courseId) {
        List<MarkVO> marks = personService.getMarks(new PersonVO(personId), new CourseVO(courseId));
        return Response.ok(marks).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Auth(roles = {AuthRole.TEACHER})
    @Path("{person_id}/marks") // FIXME: add {course_id} to path
    public Response setMarks(@PathParam("person_id") long personId, List<MarkVO> marks) {
        PersonVO personVO = new PersonVO(personId);
        personService.setMarks(personVO, marks);
        return Response.accepted().build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Auth(roles = {AuthRole.TEACHER})
    @Path("{person_id}/mark/{mark_id}")
    public Response updateMark(@PathParam("person_id") long personId, @PathParam("mark_id") long markId, @QueryParam("mark_json") String markJson) {
        Response.ResponseBuilder r;
        PersonVO person = new PersonVO();
        person.setId(personId);
        try {
            MarkVO mark = deserialize(markJson);
            mark.setId(markId);
            // FIXME: changes in PersonService
//            personService.updateMark(person, mark);
            r = Response.ok();
        } catch (JSONException e) {
            r = Response.status(Response.Status.NOT_ACCEPTABLE);
        }
        return r.build();
    }
}
