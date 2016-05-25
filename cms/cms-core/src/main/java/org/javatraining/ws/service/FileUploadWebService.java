package org.javatraining.ws.service;

import org.apache.commons.io.IOUtils;
import org.javatraining.integration.google.drive.DriveService;
import org.javatraining.integration.google.drive.FileVO;
import org.javatraining.model.LessonLinkVO;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.activation.MimetypesFileTypeMap;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Path("/file")
public class FileUploadWebService {
    @Inject
    private DriveService driveService;
    @Inject
    private MimetypesFileTypeMap mimetypesFileTypeMap;

    @POST
    @Path("/upload")
    @Consumes("multipart/form-data;charset=UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response uploadFile(MultipartFormDataInput input) {
        String fileName = "";
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("file");
        for (InputPart inputPart : inputParts) {
            try {
                MultivaluedMap<String, String> header = inputPart.getHeaders();
                fileName = getFileName(header);
                //convert the uploaded file to input stream
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
                byte[] content = IOUtils.toByteArray(inputStream);
                // add the file
                FileVO addedFileVO = driveService.addFile(new FileVO(fileName, content, mimetypesFileTypeMap.getContentType(fileName)));
                LessonLinkVO linkVO = new LessonLinkVO();
                linkVO.setDescription(addedFileVO.getTitle());
                linkVO.setLink(addedFileVO.getLink());
                return Response.status(200)
                        .entity(linkVO)
                        .build();
            } catch (IOException ignore) {
            }
        }
        return Response.ok("error with file uploading").build();
    }

    /**
     * header sample
     * {
     * Content-Type=[image/png],
     * Content-Disposition=[form-data; name="file"; filename="filename.extension"]
     * }
     */
    private String getFileName(MultivaluedMap<String, String> header) {
        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                String[] name = filename.split("=");
                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "unknown";
    }
}
