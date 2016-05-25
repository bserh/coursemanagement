package org.javatraining.integration.google.calendar.exception;

public class CalendarRoleAlreadyExistsException extends CalendarException {
    public CalendarRoleAlreadyExistsException() {
        super();
    }

    public CalendarRoleAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    public CalendarRoleAlreadyExistsException(String message) {
        super(message);
    }

    public CalendarRoleAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
