package org.javatraining.model;

/**
 * The project name is cms.
 * Created by sergey on 28.06.15 at 17:27.
 * For more information you should send mail to codedealerb@gmail.com
 */
public class Image {
    private byte[] imageBytes;

    public Image() {

    }

    public Image(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }
}
