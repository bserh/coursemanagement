package org.javatraining.ws.service;

import org.javatraining.entity.enums.PersonRole;
import org.javatraining.model.PersonDescriptionVO;
import org.javatraining.model.PersonVO;
import org.javatraining.model.mail.Mail;
import org.javatraining.model.mail.MailType;
import org.javatraining.notification.email.impl.MailNotification;
import org.javatraining.notification.sms.SMSNotificationService;
import org.javatraining.service.CourseService;
import org.javatraining.service.impl.PersonServiceImpl;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The project name is cms.
 * Created by sergey on 07.07.15 at 17:19.
 * For more information you should send mail to codedealerb@gmail.com
 */
@Path("person/course")
public class NotificationWebService {
    @EJB
    private CourseService courseService;

    @EJB
    private PersonServiceImpl personService;

    @POST
    @Path("{course-id}/notification/sms")
    @Consumes("application/json")
    public Response notifyAllWithSMS(@PathParam("course-id") long courseId, Mail mail) {
        if (!mail.getType().equals(MailType.SMS)) {
            throw new IllegalArgumentException("Mail type isn't sms");
        }
        List<PersonVO> allPersonsFromCourseByStudentRole = courseService.getAllPersonsFromCourseByRole(courseService.getCourseById(courseId), PersonRole.STUDENT);
        Stream<PersonDescriptionVO> personDescriptionVOStream = allPersonsFromCourseByStudentRole.stream().
                map(m -> personService.getPersonDescription(m.getId()));
        SMSNotificationService smsService = new SMSNotificationService();
        if (smsService.connectAndAuthToService()) {
            List<PersonDescriptionVO> descriptions = personDescriptionVOStream.collect(Collectors.toList());
            descriptions.stream().forEach(
                    descr -> smsService.sendNotificationToEndPoint(mail.getSubject(), mail.getMessage(), descr)
            );
        } else {
            return Response.serverError().build();
        }
        return Response.ok().build();
    }

    @POST
    @Path("{course-id}/notification/sms")
    @Consumes("application/json")
    public Response notifyAllWithEmail(@PathParam("course-id") long courseId, Mail mail) {
        if (!mail.getType().equals(MailType.MAIL)) {
            throw new IllegalArgumentException("Mail type isn't email");
        }
        List<PersonVO> allPersonsFromCourseByStudentRole = courseService.getAllPersonsFromCourseByRole(courseService.getCourseById(courseId), PersonRole.STUDENT);
        MailNotification mailNotification = new MailNotification();
        Stream<PersonVO> personVOStream = allPersonsFromCourseByStudentRole.stream().map(p -> personService.getById(p.getId()));
        personVOStream.forEach(
                person -> mailNotification.sendNotificationToEndPoint(mail.getSubject(), mail.getMessage(), person)
        );
        return Response.ok().build();
    }
}

