package org.javatraining.integration.google.oauth.impl;

import org.javatraining.integration.google.oauth.GoogleUserinfoService;
import org.javatraining.integration.google.oauth.exception.AuthException;
import org.javatraining.integration.google.oauth.exception.GoogleConnectionAuthException;
import org.javatraining.model.PersonVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class GoogleUserinfoServiceImpl implements GoogleUserinfoService {
    private static final Logger log = LoggerFactory.getLogger(GoogleUserinfoServiceImpl.class);
    private static final String GOOGLE_USERINFO_URL_BASE = "https://www.googleapis.com/oauth2/v2/userinfo";
    private static final String GOOGLE_PARAM_FIELDS_KEY = "fields";

    @Override
    public String getClientIdByToken(String token) {
        final String PARAM_FIELDS_VALUE = "id";
        return getUserInfoWithSpecifiedFields(token, PARAM_FIELDS_VALUE, "to get client id").getId();
    }

    @Override
    public String getEmailByToken(String token) {
        final String PARAM_FIELDS_VALUE = "email";
        return getUserInfoWithSpecifiedFields(token, PARAM_FIELDS_VALUE, "to get email").getEmail();
    }

    @Override
    public PersonVO getUserInfoByToken(String token) {
        final String PARAM_FIELDS_VALUE = "id,email,given_name,family_name";
        Userinfo userinfo = getUserInfoWithSpecifiedFields(token, PARAM_FIELDS_VALUE, "to get user info");
        PersonVO personVO = new PersonVO();
        personVO.setEmail(userinfo.getEmail());
        personVO.setName(userinfo.getGiven_name());
        personVO.setLastName(userinfo.getFamily_name());
        return personVO;
    }

    private Userinfo getUserInfoWithSpecifiedFields(final String token, final String paramFields, final String queryPurpose) throws AuthException {
        try {
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(URI.create(new URL(GOOGLE_USERINFO_URL_BASE).toExternalForm()));
            target.register(Userinfo.class);
            Response response = target
                    .queryParam(GOOGLE_PARAM_FIELDS_KEY, paramFields)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .get();
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return response.readEntity(Userinfo.class);
            }
            throw new AuthException(String.format("Response from query %s (token = %s) returns with status code %s",
                    queryPurpose, token, response.getStatus()));

        } catch (MalformedURLException e) {
            throw new GoogleConnectionAuthException(e);
        }
    }

    private static class Userinfo {
        private String id;
        private String email;
        private String verified_email;
        private String name;
        private String given_name;
        private String family_name;
        private String link;
        private String picture;
        private String gender;
        private String locale;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getGiven_name() {
            return given_name;
        }

        public void setGiven_name(String given_name) {
            this.given_name = given_name;
        }

        public String getFamily_name() {
            return family_name;
        }

        public void setFamily_name(String family_name) {
            this.family_name = family_name;
        }

        public String getVerified_email() {
            return verified_email;
        }

        public void setVerified_email(String verified_email) {
            this.verified_email = verified_email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getLocale() {
            return locale;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }

        @Override
        public String toString() {
            return "Userinfo{" +
                    "id='" + id + '\'' +
                    ", email='" + email + '\'' +
                    ", given_name='" + given_name + '\'' +
                    ", family_name='" + family_name + '\'' +
                    '}';
        }
    }
}
