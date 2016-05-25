package org.javatraining.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;
import java.util.Set;

@Entity
@Table(name = "courses", schema = "")
public class CourseEntity implements Serializable, GenericEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    private Long id;

    @NotNull
    @Basic
    @Column(name = "name", nullable = false, insertable = true, updatable = true, length = 255)
    private String name;

    @Basic
    @Column(name = "calendar_id", nullable = true, insertable = true, updatable = true, length = 255)
    private String calendar_id;

    @NotNull
    @Basic
    @Column(name = "description", nullable = false, insertable = true, updatable = true, length = 16777215)
    private String description;

    @NotNull
    @Basic
    @Column(name = "startdate", nullable = false, insertable = true, updatable = true)
    private Date startdate;

    @NotNull
    @Basic
    @Column(name = "enddate", nullable = false, insertable = true, updatable = true)
    private Date enddate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course", cascade = {CascadeType.REMOVE})
    private Set<LessonEntity> lessons;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "courses", cascade = {CascadeType.REMOVE})
    private Set<NewsEntity> news;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course", cascade = {CascadeType.REMOVE})
    private Set<CoursePersonStatusEntity> coursePersonEntities;


    public CourseEntity() {
    }

    public CourseEntity(String name, String description, Date startdate, Date enddate) {
        this.name = name;
        this.description = description;
        this.startdate = startdate;
        this.enddate = enddate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCalendar_id() {
        return calendar_id;
    }

    public void setCalendar_id(String calendar_id) {
        this.calendar_id = calendar_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartDate(Date startdate) {
        this.startdate = startdate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEndDate(Date enddate) {
        this.enddate = enddate;
    }

    public Set<NewsEntity> getNews() {
        return news;
    }

    public void setNews(Set<NewsEntity> news) {
        this.news = news;
    }

    public Set<LessonEntity> getLessons() {
        return lessons;
    }

    public void setLessons(Set<LessonEntity> lessons) {
        this.lessons = lessons;
    }

    public Set<CoursePersonStatusEntity> getCoursePersonEntities() {
        return coursePersonEntities;
    }

    public void setCoursePersonEntities(Set<CoursePersonStatusEntity> coursePersonEntities) {
        this.coursePersonEntities = coursePersonEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseEntity)) return false;

        CourseEntity that = (CourseEntity) o;

        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (enddate != null ? !enddate.equals(that.enddate) : that.enddate != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (startdate != null ? !startdate.equals(that.startdate) : that.startdate != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (startdate != null ? startdate.hashCode() : 0);
        result = 31 * result + (enddate != null ? enddate.hashCode() : 0);
        return result;
    }
}
