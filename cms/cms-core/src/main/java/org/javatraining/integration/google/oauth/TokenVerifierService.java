package org.javatraining.integration.google.oauth;

public interface TokenVerifierService {
    boolean isTokenValid(String token);
}
