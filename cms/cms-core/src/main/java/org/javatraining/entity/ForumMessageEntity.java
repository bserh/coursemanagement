package org.javatraining.entity;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "forum_messages", schema = "")
public class ForumMessageEntity implements Serializable, GenericEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    private Long id;

    @NotNull
    @Basic
    @Column(name = "parent_id", nullable = false, insertable = true, updatable = true)
    private Long parentId;

    @NotNull
    @Basic
    @Column(name = "title", nullable = false, insertable = true, updatable = true, length = 255)
    private String title;

    @NotNull
    @Basic
    @Column(name = "description", nullable = false, insertable = true, updatable = true, length = 16777215)
    private String description;

    @NotNull
    @Basic
    @Column(name = "date", nullable = false, insertable = true, updatable = true)
    private Timestamp date;

    public ForumMessageEntity() {
    }

    @ManyToOne(cascade = {})
    @JoinColumn(name = "lesson_id", nullable = false)
    private LessonEntity lessons;

    @ManyToOne(cascade = {})
    @JoinColumn(name = "author", nullable = false)
    private PersonEntity persons;

    public ForumMessageEntity(Long parentId, String title, String description, Timestamp date) {
        this.parentId = parentId;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public LessonEntity getLessons() {
        return lessons;
    }

    public void setLessons(LessonEntity lessons) {
        this.lessons = lessons;
    }

    public PersonEntity getPersons() {
        return persons;
    }

    public void setPersons(PersonEntity persons) {
        this.persons = persons;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String content) {
        this.description = content;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ForumMessageEntity)) return false;

        ForumMessageEntity that = (ForumMessageEntity) o;

        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        if (title != null ? !title.equals(that.title) : that.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (parentId != null ? parentId.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);

        return result;
    }
}