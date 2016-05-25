package org.javatraining.model.mail;

/**
 * The project name is cms.
 * Created by sergey on 07.07.15 at 17:26.
 * For more information you should send mail to codedealerb@gmail.com
 */
public class Mail {
    private MailType mailtype;
    private String message;
    private String subject;
    public Mail() {

    }

    public Mail(MailType type, String message) {
        this.setType(type);
        this.setMessage(message);
    }

    public MailType getType() {
        return mailtype;
    }

    public void setType(MailType type) {
        this.mailtype = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}

