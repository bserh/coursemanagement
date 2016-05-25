package org.javatraining.ws.service;

import org.javatraining.auth.Auth;
import org.javatraining.config.AuthRole;
import org.javatraining.model.LessonLinkVO;
import org.javatraining.service.LessonLinkService;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by asudak on 6/24/15.
 */
@Path("course")
public class LessonLinkWebService {
    @EJB
    LessonLinkService lessonLinkService;

    @GET
    @Path("{course_id}/lesson/{lesson_order_num}/link")
    @Produces(MediaType.APPLICATION_JSON)
    @Auth(roles = {AuthRole.STUDENT, AuthRole.TEACHER})
    public Response getLessons(@PathParam("course_id") Long courseId, @PathParam("lesson_order_num") Long lessonOrderNum) {
        return Response.ok(lessonLinkService.getByLessonId(courseId, lessonOrderNum)).build();
    }

    @POST
    @Path("{course_id}/lesson/{lesson_order_num}/link")
    @Consumes(MediaType.APPLICATION_JSON)
    @Auth(roles = {AuthRole.TEACHER})
    public Response saveLesson(@Context UriInfo uriInfo, @PathParam("course_id") Long courseId, @PathParam("lesson_order_num") Long lessonOrderNum, LessonLinkVO lessonLink) {
        Response.ResponseBuilder r;
        try {
            lessonLink.setId(null); //make sure that there no id set.

            lessonLinkService.save(lessonLink);

            String lessonLinkUri = uriInfo.getRequestUri().toString() + "/" + lessonLink.getId();
            r = Response.created(new URI(lessonLinkUri));
        } catch (NullPointerException e) {
            //seems LessonLink wasn't saved
            r = Response.serverError(); //FIXME find error code that will describe error better
        } catch (URISyntaxException e) {
            //this shouldn't happen
            r = Response.serverError();
        }
        return r.build();
    }

    @GET
    @Path("{course_id}/lesson/{lesson_order_num}/link/{order_num}")
    @Produces(MediaType.APPLICATION_JSON)
    @Auth(roles = {AuthRole.STUDENT, AuthRole.TEACHER})
    public Response getLessonByOrderNum(@PathParam("course_id") Long courseId, @PathParam("lesson_order_num") Long lessonOrderNum, @PathParam("order_num") Long orderNum) {
        return Response.ok(lessonLinkService.getByOrderNum(courseId, lessonOrderNum, orderNum)).build();
    }

    @PUT
    @Path("{course_id}/lesson/{lesson_order_num}/link/{order_num}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Auth(roles = {AuthRole.TEACHER})
    public Response updateLessonByOrderNum(@PathParam("course_id") Long courseId, @PathParam("lesson_order_num") Long lessonOrderNum, @PathParam("order_num") Long orderNum, LessonLinkVO lessonLink) {
        lessonLinkService.updateByOrderNum(courseId, lessonOrderNum, orderNum, lessonLink);
        return Response.ok().build();
    }

    @DELETE
    @Path("{course_id}/lesson/{lesson_order_num}/link/{order_num}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Auth(roles = {AuthRole.TEACHER})
    public Response deleteLessonByOrderNum(@PathParam("course_id") Long courseId, @PathParam("lesson_order_num") Long lessonOrderNum, @PathParam("order_num") Long orderNum) {
        lessonLinkService.deleteByOrderNum(courseId, lessonOrderNum, orderNum);
        return Response.ok().build();
    }
}
