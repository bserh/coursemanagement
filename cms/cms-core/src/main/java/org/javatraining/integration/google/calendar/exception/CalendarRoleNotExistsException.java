package org.javatraining.integration.google.calendar.exception;

public class CalendarRoleNotExistsException extends CalendarException {
    public CalendarRoleNotExistsException() {
        super();
    }

    public CalendarRoleNotExistsException(Throwable cause) {
        super(cause);
    }

    public CalendarRoleNotExistsException(String message) {
        super(message);
    }

    public CalendarRoleNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
