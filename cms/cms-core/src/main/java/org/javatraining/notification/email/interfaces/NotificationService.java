package org.javatraining.notification.email.interfaces;

import org.javatraining.model.PersonVO;

/**
 * The project name is cms.
 * Created by sergey on 01.07.15 at 17:41.
 * For more information you should send mail to codedealerb@gmail.com
 */
public interface NotificationService<T extends PersonVO> {

    void sendNotificationToEndPoint(String subject, String message, T user);
}
