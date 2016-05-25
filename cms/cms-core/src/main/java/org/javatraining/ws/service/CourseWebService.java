package org.javatraining.ws.service;

import flexjson.JSONException;
import org.javatraining.auth.Auth;
import org.javatraining.config.AuthRole;
import org.javatraining.config.Config;
import org.javatraining.integration.google.calendar.CalendarService;
import org.javatraining.integration.google.calendar.CalendarVO;
import org.javatraining.integration.google.calendar.exception.CalendarException;
import org.javatraining.model.CoursePersonStatusVO;
import org.javatraining.model.CourseVO;
import org.javatraining.model.CourseWithDetailsVO;
import org.javatraining.model.PersonVO;
import org.javatraining.service.CourseService;
import org.javatraining.service.PersonService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

/**
 * Created by asudak on 04.06.15.
 */
@Stateless
@Path("course")
public class CourseWebService extends AbstractWebService<CourseVO> {
    private static final Long NONE_COURSE_PROTOTYPE = -1L;
    private static final String COURSES_STARTED_AFTER_DATE = "start_after";
    private static final String COURSES_ENDED_BEFORE_DATE = "end_before";
    @EJB
    private CourseService courseService;
    @EJB
    private PersonService personService;
    @Inject
    private CalendarService calendarService;

    CourseWebService() {
        super(CourseVO.class);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCoursesList(@QueryParam("date") String curDate, @QueryParam("period") String period) {
        List<CourseVO> courses;
        if (curDate == null) {
            courses = courseService.getAll();
        } else {
            switch (period) {
                case COURSES_STARTED_AFTER_DATE:
                    courses = courseService.getAllStartedAfterDate(new Date());
                    break;
                case COURSES_ENDED_BEFORE_DATE:
                    courses = courseService.getAllEndedBeforeDate(new Date());
                    break;
                default:
                    return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        return Response.ok(courses).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{course_id}")
    public Response getCourse(@PathParam("course_id") long courseId) {
        CourseVO course = courseService.getCourseById(courseId);
        Response.ResponseBuilder r;
        if (course == null)
            r = Response.noContent();
        else
            r = Response.ok(serialize(course));
        return r.build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Auth(roles = {AuthRole.TEACHER})
    public Response createCourse(@Context UriInfo uriInfo, CourseWithDetailsVO courseVO,
                                 @DefaultValue("-1") @QueryParam(value = "prototypeId") String prototypeId) {
        Response.ResponseBuilder r;
        try {
            Long coursePrototypeId = Long.parseLong(prototypeId);
            if (coursePrototypeId.equals(NONE_COURSE_PROTOTYPE)) {
                courseService.save(courseVO);
            } else {
                CourseVO coursePrototype = new CourseVO();
                coursePrototype.setId(coursePrototypeId);
                courseService.createFromPrototype(courseVO, coursePrototype);
            }
            CalendarVO courseCalendar = calendarService.addCalendar(new CalendarVO(courseVO.getName(), courseVO.getTeachers()));
            courseVO.setCalendarId(courseCalendar.getId());
            courseService.updateCourse(courseVO);
            String courseUri = uriInfo.getRequestUri().toString() + "/" + courseVO.getId();
            r = Response.created(new URI(courseUri)).entity(courseVO);
        } catch (NumberFormatException e) {
            r = Response.status(Response.Status.BAD_REQUEST);
        } catch (JSONException e) {
            r = Response.status(Response.Status.NOT_ACCEPTABLE);
        } catch (URISyntaxException | CalendarException e) {
            //this shouldn't happen
            r = Response.serverError();
        }

        return r.build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Auth(roles = {AuthRole.TEACHER})
    @Path("{course_id}")
    public Response updateCourse(@PathParam("course_id") long courseId, CourseVO courseVO) {
        Response.ResponseBuilder r;
        try {
            courseVO.setId(courseId);
            courseService.updateCourse(courseVO);
            r = Response.ok();
        } catch (JSONException e) {
            r = Response.status(Response.Status.NOT_ACCEPTABLE);
        }
        return r.build();
    }

    @DELETE
    @Path("{course_id}")
    @Auth(roles = {AuthRole.TEACHER})
    public Response deleteCourse(@PathParam("course_id") long courseId) {
        Response.ResponseBuilder r;
        CourseVO course = new CourseVO();
        course.setId(courseId);
        try {
            courseService.removeCourse(course);
            r = Response.ok();
        } catch (Exception e) {
            r = Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_HTML_TYPE)
                    .entity(String.format("Exception: %s, description: %s", e.getClass().getName(), e.getMessage()));
        }
        return r.build();
    }

    @GET
    @Path("{course_id}/subscriber")
    @Auth(roles = {AuthRole.TEACHER})
    public Response getSubscribers(@PathParam("course_id") long courseId) {
        CourseVO course = new CourseVO();
        course.setId(courseId);
        List<CoursePersonStatusVO> persons = courseService.getSubscribersWithStatusesForCourse(course);
        return Response.ok(serialize(persons)).build();
    }

    @PUT
    @Path("{course_id}/subscriber")
    @Auth(roles = {AuthRole.TEACHER})
    public Response updateSubscribers(@PathParam("course_id") long courseId, List<CoursePersonStatusVO> statusVOs) {
        statusVOs.forEach(coursePersonStatusVO -> coursePersonStatusVO.setCourseId(courseId));
        statusVOs.forEach(personService::updatePersonStatusOnCourse);
        return Response.accepted().build();
    }

    @GET
    @Path("{course_id}/student")
    @Auth(roles = {AuthRole.TEACHER})
    public Response getStudentsWithMarks(@PathParam("course_id") long courseId) {
        CourseVO course = new CourseVO();
        course.setId(courseId);
        List<PersonVO> persons = courseService.getAllStudentsWithMarksFromCourse(course);
        return Response.ok(serialize(persons)).build();
    }

    @PUT
    @Path("{course_id}/subscribe")
    @Auth(roles = {AuthRole.STUDENT})
    public Response subscribeCourse(@PathParam("course_id") long courseId, PersonVO person) {
        CourseVO course = new CourseVO();
        course.setId(courseId);
        personService.addPersonRequestForCourse(person, course);
        return Response.accepted().build();
    }

    @PUT
    @Path("{course_id}/unsubscribe")
    @Auth(roles = {AuthRole.STUDENT})
    public Response unsubscribeCourse(@PathParam("course_id") long courseId, @HeaderParam(Config.REQUEST_HEADER_ID) long userId) {
        PersonVO person = new PersonVO();
        person.setId(userId);
        CourseVO course = new CourseVO();
        course.setId(courseId);
        personService.removePersonRequestForCourse(person, course);
        return Response.accepted().build();
    }
}
