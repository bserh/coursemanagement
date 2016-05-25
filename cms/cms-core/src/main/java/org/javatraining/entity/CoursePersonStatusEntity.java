package org.javatraining.entity;


import org.javatraining.entity.enums.CourseStatus;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = " course_person_status", schema = "")
public class CoursePersonStatusEntity implements Serializable, GenericEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "course_status", nullable = false, insertable = true, updatable = true, length = 255)
    private CourseStatus courseStatus;

    @ManyToOne(cascade = {})
    @JoinColumn(name = "course_id", nullable = false)
    private CourseEntity course;

    @ManyToOne(cascade = {})
    @JoinColumn(name = "person_id", nullable = false)
    private PersonEntity person;

    public CoursePersonStatusEntity() {
    }

    public CoursePersonStatusEntity(CourseStatus courseStatus) {
        this.courseStatus = courseStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CourseStatus getCourseStatus() {
        return courseStatus;
    }

    public void setCourseStatus(CourseStatus courseStatus) {
        this.courseStatus = courseStatus;
    }

    public CourseEntity getCourse() {
        return course;
    }

    public void setCourse(CourseEntity course) {
        this.course = course;
    }

    public PersonEntity getPerson() {
        return person;
    }

    public void setPerson(PersonEntity person) {
        this.person = person;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CoursePersonStatusEntity)) return false;

        CoursePersonStatusEntity that = (CoursePersonStatusEntity) o;

        if (courseStatus != that.courseStatus) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (courseStatus != null ? courseStatus.hashCode() : 0);
        return result;
    }
}
