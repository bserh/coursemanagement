package org.javatraining.integration.gitlab.impl;

import org.apache.velocity.app.VelocityEngine;
import org.javatraining.integration.gitlab.api.model.GitLabUser;
import org.javatraining.notification.email.impl.MailNotification;
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
 * Created by sergey on 17.06.15 at 13:26.
 * For more information you should send mail to codedealerb@gmail.com
 */
public class GitLabNotificationServiceImpl implements NotificationService<GitLabUser> {
    @Override
    public void sendNotificationToEndPoint(String subject, String message, GitLabUser user) {
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

            String from = (String) propertiesMarshaler.get("email_for_gitlab_notification");
            String password = (String) propertiesMarshaler.get("password_for_gitlab_notification");

            Session session = Session.getDefaultInstance(props,
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(from, password);
                        }
                    });

            Transport transport = session.getTransport();
            InternetAddress addressFrom = new InternetAddress(from);

            VelocityEngine velocityEngine = MailNotification.createEngine();

            MimeMessage mMessage = new MimeMessage(session);
            mMessage.setSender(addressFrom);
            mMessage.setSubject(subject);

            Map model = new HashMap<>();
            model.put("person.Properties", user);
            model.put("text", message);

            mMessage.setContent(VelocityEngineUtils.mergeTemplateIntoString(
                            velocityEngine, "../resources/velocity/gitlab-mail-template.html", "UTF-8", model),
                    "text/html; charset=UTF-8");
            mMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));

            transport.connect();
            Transport.send(mMessage);
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
