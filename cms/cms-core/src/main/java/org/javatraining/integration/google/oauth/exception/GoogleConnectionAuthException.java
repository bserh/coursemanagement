package org.javatraining.integration.google.oauth.exception;

public class GoogleConnectionAuthException extends AuthException {

    public GoogleConnectionAuthException() {
        super();
    }

    public GoogleConnectionAuthException(Throwable cause) {
        super(cause);
    }

    public GoogleConnectionAuthException(String message) {
        super(message);
    }

    public GoogleConnectionAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
