package org.javatraining.model;


import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by vika on 30.05.15.
 */
public class NewsVO implements Serializable {
    private Long id;
    @NotNull
    private String title;
    @NotNull
    private String content;
    @NotNull
    private Timestamp date;
    @NotNull
    private Long courseId;

    public NewsVO() {
    }

    public NewsVO(String title, String content, Timestamp date) {
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public NewsVO(Long id, String title, String content, Timestamp date) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewsVO newsVO = (NewsVO) o;

        if (id != null ? !id.equals(newsVO.id) : newsVO.id != null) return false;
        if (title != null ? !title.equals(newsVO.title) : newsVO.title != null) return false;
        if (content != null ? !content.equals(newsVO.content) : newsVO.content != null) return false;
        return !(date != null ? !date.equals(newsVO.date) : newsVO.date != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
