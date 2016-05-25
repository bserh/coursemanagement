package org.javatraining.integration.google.drive;

import org.javatraining.model.PersonVO;

import javax.validation.constraints.NotNull;

public interface DriveService {

    FileVO addFile(@NotNull FileVO fileVO);

    /**
     * Remove the file from the drive. The method is allowed only for removing
     * the files, which were added by the application. All files, which were added by
     * the owner, can be deleted only by it.
     *
     * @param fileVO new file to add
     */
    void removeFile(@NotNull FileVO fileVO);

    /**
     * Set owner access to all files. At the same time the files
     * can have only one owner, so when owner is set, application
     * can't remove it and has only permissions to edit the files.
     *
     * @param personVO new owner of the files
     */
    void changeRootFolderOwner(@NotNull PersonVO personVO);

    void getFilesList();

    void removeFiles();
}
