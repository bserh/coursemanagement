package org.javatraining.integration.google;

import javax.activation.MimetypesFileTypeMap;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.io.IOException;

@ApplicationScoped
public class MimeTypeFileTypeMapperFactory {
    private static final String PATH_TO_MIME_TYPES_FILE = "WEB-INF/classes/service_account/mime.types";

    @Inject
    private ServletContext servletContext;

    @Produces
    public MimetypesFileTypeMap getMapper() {
        try {
            return new MimetypesFileTypeMap(servletContext.getRealPath(PATH_TO_MIME_TYPES_FILE));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
