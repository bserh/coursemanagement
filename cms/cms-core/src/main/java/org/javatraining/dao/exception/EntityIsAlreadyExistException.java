package org.javatraining.dao.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = false)
public class EntityIsAlreadyExistException extends RuntimeException {
    public EntityIsAlreadyExistException() {
        super();
    }

    public EntityIsAlreadyExistException(Throwable cause) {
        super(cause);
    }

    public EntityIsAlreadyExistException(String message) {
        super(message);
    }

    public EntityIsAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public Exception getCausedByException() {
        return (Exception) getCause();
    }
}
