package org.javatraining.integration.google.drive;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Permission;
import org.javatraining.integration.google.drive.exception.DriveException;
import org.javatraining.model.PersonVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@ApplicationScoped
public class DriveServiceImpl implements DriveService {
    private static final Logger log = LoggerFactory.getLogger(DriveServiceImpl.class);
    private static final String PARENT_FOLDER_NAME = "JavaTraining";
    private static final String FOLDER_MIME_TYPE = "application/vnd.google-apps.folder";
    @Inject
    private Drive googleDriveService;

    @Override
    public void getFilesList() {
        try {
            FileList fileList = googleDriveService.files().list().execute();
            if (fileList.getItems() != null) {
                log.debug("Files from service account's drive: ");
                for (File file : fileList.getItems()) {
                    log.debug(file.getTitle());
                }
            }
        } catch (IOException e) {
            throw new DriveException(e);
        }
    }

    @Override
    public void removeFiles() {
        try {
            FileList fileList = googleDriveService.files().list().execute();
            if (fileList.getItems() != null) {
                for (File file : fileList.getItems()) {
                    if (!(file.getMimeType().equals(FOLDER_MIME_TYPE) && (file.getTitle().equals(PARENT_FOLDER_NAME)))) {
                        removeFile(new FileVO(file.getId(), file.getAlternateLink()));
                    }
                }
            }
        } catch (IOException e) {
            throw new DriveException(e);
        }
    }

    @Override
    public FileVO addFile(@NotNull FileVO fileVO) {
        try {
            String parentFolderId = getRootFolderId(true);
            File fileMetadata = new File();
            fileMetadata.setTitle(fileVO.getTitle());
            ByteArrayContent byteArrayContent = new ByteArrayContent(fileVO.getMimeType(), fileVO.getContent());
            File addedFile = googleDriveService.files()
                    .insert(fileMetadata, byteArrayContent)
                    .execute();
            googleDriveService.parents()
                    .insert(addedFile.getId(), new ParentReference().setId(parentFolderId))
                    .execute();
            log.debug("Link to added file: {}", addedFile.getAlternateLink());
            return new FileVO(addedFile.getId(), fileVO.getTitle(), addedFile.getAlternateLink());
        } catch (IOException e) {
            throw new DriveException(e);
        }
    }

    @Override
    public void removeFile(@NotNull FileVO fileVO) {
        try {
            googleDriveService.files().delete(fileVO.getId()).execute();
        } catch (IOException e) {
            throw new DriveException(e);
        }
    }

    @Override
    public void changeRootFolderOwner(@NotNull PersonVO personVO) {
        try {
            String folderId = getRootFolderId(true);
            if (personVO.getEmail() != null) {
                googleDriveService.permissions()
                        .insert(folderId, new Permission().setRole("owner").setType("user").setValue(personVO.getEmail()))
                        .execute();

            }
        } catch (IOException e) {
            throw new DriveException(e);
        }
    }

    private String getRootFolderId(boolean createIfNotExists) throws IOException {
        FileList fileList = googleDriveService.files().list().execute();
        if (fileList != null) {
            for (File file : fileList.getItems()) {
                if (file.getMimeType().equals(FOLDER_MIME_TYPE) && file.getTitle().equals(PARENT_FOLDER_NAME)) {
                    return file.getId();
                }
            }
        }
        if (!createIfNotExists) {
            return null;
        }
        File addedDir = googleDriveService.files()
                .insert(new File().setTitle(PARENT_FOLDER_NAME).setMimeType(FOLDER_MIME_TYPE))
                .execute();
        googleDriveService.permissions()
                .insert(addedDir.getId(), new Permission().setRole("reader").setType("anyone").setValue("").setWithLink(true))
                .execute();
        log.debug("Link to parent directory: {}", addedDir.getAlternateLink());
        log.debug("Parent directory id: {}", addedDir.getId());
        return addedDir.getId();
    }
}
