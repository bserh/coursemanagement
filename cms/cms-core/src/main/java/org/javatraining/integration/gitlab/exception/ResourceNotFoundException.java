package org.javatraining.integration.gitlab.exception;

/**
 * The project name is cms.
 * Created by sergey on 02.06.15 at 15:02.
 * For more information you should send mail to codedealerb@gmail.com
 */
public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException() {

    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
