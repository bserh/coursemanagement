package org.javatraining.service;

import org.javatraining.model.PersonDescriptionVO;
import org.javatraining.notification.sms.SMSNotificationService;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * The project name is cms.
 * Created by sergey on 02.07.15 at 23:42.
 * For more information you should send mail to codedealerb@gmail.com
 */

public class SMSNotificationServiceTest {
    @Test
    public void testSendMessageToTestPhoneNumber(){
        SMSNotificationService service = new SMSNotificationService();
        assertTrue(service.connectAndAuthToService());
        PersonDescriptionVO person = new PersonDescriptionVO();
        person.setId((long) 1);
        person.setPhoneNumber("380981348966");
        service.sendNotificationToEndPoint("Hello", "World", person);
    }
}
