package org.javatraining.notification.sms;

import org.javatraining.model.PersonDescriptionVO;
import org.javatraining.notification.email.interfaces.NotificationService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

/**
 * The project name is cms.
 * Created by sergey on 02.07.15 at 23:02.
 * For more information you should send mail to codedealerb@gmail.com
 */
    public class SMSNotificationService implements NotificationService<PersonDescriptionVO> {
        private ServiceSoap smsService;

        public boolean connectAndAuthToService() {
            smsService = new Service().getServiceSoap();
            JSONParser parser = new JSONParser();
            try {
                JSONObject propertiesMarshaler = (JSONObject) parser.parse(new FileReader(
                        "..\\..\\..\\src\\main\\resources\\mail\\account_properties"));//FIXME FIX PATH!!!!!!!!!!
                String from = (String) propertiesMarshaler.get("email_for_notification");
                String password = (String) propertiesMarshaler.get("password_for_notification");

                String result = smsService.auth(from, password);
                switch (result) {
                    case "Вы успешно авторизировались":
                        return true;
                    //FIXME https://turbosms.ua/soap.html else
                }

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        public void sendNotificationToEndPoint(String subject, String message, PersonDescriptionVO user) {
            SendSMS sms = new SendSMS();
            JSONParser parser = new JSONParser();
            try {
                JSONObject propertiesMarshaler = (JSONObject) parser.parse(new FileReader(
                        "../resources/mail/account_properties"));

                sms.setSender((String) propertiesMarshaler.get("account_phone_number"));
                sms.setDestination(user.getPhoneNumber());
                sms.setText(message);

                smsService.sendSMS(sms.getSender(), sms.getDestination(), sms.getText(), null);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

