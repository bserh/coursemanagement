package org.javatraining.notification.email.impl;

import org.apache.velocity.app.VelocityEngine;
import org.javatraining.model.PersonVO;
import org.javatraining.notification.email.interfaces.NotificationService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * The project name is cms.
 * Created by sergey on 30.06.15 at 18:06.
 * For more information you should send mail to codedealerb@gmail.com
 */
public class MailNotification implements NotificationService<PersonVO> {

    public static VelocityEngine createEngine() throws Exception {
        VelocityEngine ve = new VelocityEngine();
        Properties p = new Properties();
        p.setProperty("resource.loader", "class");
        p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.init(p);
        return ve;
    }

    @Override
    public void sendNotificationToEndPoint(String subject, String mail, PersonVO user) {
        Properties props = new Properties();
        JSONParser parser = new JSONParser();
        try {
            JSONObject propertiesMarshaler = (JSONObject) parser.parse(new FileReader(
                    "mail/mail_properties"));

            props.put("mail.transport.protocol", propertiesMarshaler.get("mail.transport.protocol"));
            props.put("mail.host", propertiesMarshaler.get("mail.host"));
            props.put("mail.smtp.auth", propertiesMarshaler.get("mail.smtp.auth"));
            props.put("mail.smtp.port", propertiesMarshaler.get("mail.smtp.port"));
            props.put("mail.debug", propertiesMarshaler.get("mail.debug"));
            props.put("mail.smtp.socketFactory.port", propertiesMarshaler.get("mail.smtp.socketFactory.port"));
            props.put("mail.smtp.socketFactory.class", propertiesMarshaler.get("mail.smtp.socketFactory.class"));
            props.put("mail.smtp.socketFactory.fallback", propertiesMarshaler.get("mail.smtp.socketFactory.fallback"));

            propertiesMarshaler = (JSONObject) parser.parse(new FileReader(
                    "../resources/mail/account_properties.json"));

            String from = (String) propertiesMarshaler.get("email_for_notification");
            String password = (String) propertiesMarshaler.get("password_for_notification");

            Session session = Session.getDefaultInstance(props,
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(from, password);
                        }
                    });

            Transport transport = session.getTransport();
            InternetAddress addressFrom = new InternetAddress(from);

            VelocityEngine velocityEngine = createEngine();

            MimeMessage message = new MimeMessage(session);
            message.setSender(addressFrom);
            message.setSubject(subject);

            Map model = new HashMap<>();
            model.put("person.Properties", user);
            model.put("message", message);

            message.setContent(VelocityEngineUtils.mergeTemplateIntoString(
                            velocityEngine, "../resources/velocity/mail.html", "UTF-8", model),
                    "text/html; charset=UTF-8");
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));

            transport.connect();
            Transport.send(message);
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
