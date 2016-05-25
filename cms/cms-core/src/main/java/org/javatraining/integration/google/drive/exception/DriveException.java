package org.javatraining.integration.google.drive.exception;

public class DriveException extends RuntimeException {
    public DriveException() {
        super();
    }

    public DriveException(Throwable cause) {
        super(cause);
    }

    public DriveException(String message) {
        super(message);
    }

    public DriveException(String message, Throwable cause) {
        super(message, cause);
    }
}
