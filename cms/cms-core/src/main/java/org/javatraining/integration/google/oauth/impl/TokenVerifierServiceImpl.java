package org.javatraining.integration.google.oauth.impl;

import org.javatraining.integration.google.oauth.TokenVerifierService;
import org.javatraining.integration.google.oauth.exception.GoogleConnectionAuthException;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

@ApplicationScoped
public class TokenVerifierServiceImpl implements TokenVerifierService {
    private static final String BASE = "https://www.googleapis.com/oauth2/v1/tokeninfo";

    @Override
    public boolean isTokenValid(String token) {
        try {
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(URI.create(new URL(BASE).toExternalForm()));
            Response response = target
                    .queryParam("access_token", token)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            return response.getStatus() == Response.Status.OK.getStatusCode();
        } catch (MalformedURLException e) {
            throw new GoogleConnectionAuthException(e);
        }
    }
}
