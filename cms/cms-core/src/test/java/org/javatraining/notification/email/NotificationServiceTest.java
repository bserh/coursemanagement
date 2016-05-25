package org.javatraining.notification.email;

import org.javatraining.model.PersonVO;
import org.javatraining.notification.email.impl.MailNotification;
import org.javatraining.notification.email.interfaces.NotificationService;
import org.junit.Ignore;
import org.junit.Test;

/**
 * The project name is cms.
 * Created by sergey on 02.07.15 at 19:29.
 * For more information you should send mail to codedealerb@gmail.com
 */
@Ignore
public class NotificationServiceTest {
    @Test
    public void testSendMessageToTestEmail() {
        NotificationService<PersonVO> mail = new MailNotification();
        PersonVO vo = new PersonVO((long) 1);
        vo.setEmail("valet12353@gmail.com");//test email
        mail.sendNotificationToEndPoint("testSubject", "test", vo);
    }
}
