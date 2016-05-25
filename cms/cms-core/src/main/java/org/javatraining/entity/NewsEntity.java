package org.javatraining.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "news", schema = "")
public class NewsEntity implements Serializable, GenericEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    private Long id;

    @NotNull
    @Basic
    @Column(name = "title", nullable = false, insertable = true, updatable = true, length = 255)
    private String title;

    @NotNull
    @Basic
    @Column(name = "description", nullable = false, insertable = true, updatable = true, length = 16777215)
    private String content;

    @NotNull
    @Basic
    @Column(name = "date", nullable = false, insertable = true, updatable = true)
    private Timestamp date;

    @ManyToOne(cascade = {})
    @JoinColumn(name = "course_id", nullable = false)
    private CourseEntity courses;

    public NewsEntity() {
    }

    public NewsEntity(String title, String content, Timestamp date, CourseEntity courses) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.courses = courses;
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

    public void setDescription(String content) {
        this.content = content;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public CourseEntity getCourses() {
        return courses;
    }

    public void setCourse(CourseEntity courses) {
        this.courses = courses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewsEntity)) return false;

        NewsEntity that = (NewsEntity) o;

        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (courses != null ? !courses.equals(that.courses) : that.courses != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (courses != null ? courses.hashCode() : 0);
        return result;
    }
}
