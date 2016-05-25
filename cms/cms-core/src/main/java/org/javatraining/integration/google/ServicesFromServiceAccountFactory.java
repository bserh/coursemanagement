package org.javatraining.integration.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import org.javatraining.integration.google.calendar.exception.CalendarException;
import org.javatraining.integration.google.drive.exception.DriveException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Set;

@ApplicationScoped
public class ServicesFromServiceAccountFactory {
    private static final String EMAIL_ADDRESS = "413719209435-kgvn4ti1mv0q6jh754thrh4kl3svra04@developer.gserviceaccount.com";
    private static final String APPLICATION_NAME = "AppWithServiceAccount";
    private static final String PATH_TO_PRIVATE_KEY_FILE = "WEB-INF/classes/service_account/secret.p12";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @Inject
    private ServletContext servletContext;

    @Produces
    public Calendar getCalendarService() {
        try {
            return new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, getCredential(Collections.singleton(CalendarScopes.CALENDAR)))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new CalendarException(e);
        }
    }

    @Produces
    public Drive getDriveService() {
        try {
            return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, getCredential(DriveScopes.all()))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new DriveException(e);
        }
    }

    private GoogleCredential getCredential(Set<String> scopes) throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(EMAIL_ADDRESS)
                .setServiceAccountPrivateKeyFromP12File(new File(servletContext.getRealPath(PATH_TO_PRIVATE_KEY_FILE)))
                .setServiceAccountScopes(scopes)
                .build();
        return credential;
    }

    private ServicesFromServiceAccountFactory() {
    }
}
