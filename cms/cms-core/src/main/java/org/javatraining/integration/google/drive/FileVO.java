package org.javatraining.integration.google.drive;

import javax.validation.constraints.NotNull;

public class FileVO {
    private String id;
    private String title;
    private byte[] content;
    private String mimeType;
    private String link;

    public FileVO() {

    }

    public FileVO(@NotNull String title, @NotNull byte[] content, @NotNull String mimeType) {
        this.title = title;
        this.content = content;
        this.mimeType = mimeType;
    }

    public FileVO(@NotNull String id, String link) {
        this.id = id;
        this.link = link;
    }

    public FileVO(@NotNull String id, @NotNull String title, @NotNull String link) {
        this.id = id;
        this.title = title;
        this.link = link;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(@NotNull String title) {
        this.title = title;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(@NotNull byte[] content) {
        this.content = content;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(@NotNull String mimeType) {
        this.mimeType = mimeType;
    }

    public String getLink() {
        return link;
    }

    public void setLink(@NotNull String link) {
        this.link = link;
    }

}
