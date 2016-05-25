package org.javatraining.model;

import org.javatraining.entity.enums.CourseStatus;

import javax.validation.constraints.NotNull;

/**
 * Created by vika on 27.06.15.
 */
public class CoursePersonStatusVO {

    private Long id;
    @NotNull
    private CourseStatus courseStatus;
    @NotNull
    private Long courseId;
    @NotNull
    private Long personId;
    private String personLastName;
    private String personFirstName;

    public CoursePersonStatusVO() {
    }

    public CoursePersonStatusVO(CourseStatus courseStatus, Long courseId, Long personId) {
        this.courseStatus = courseStatus;
        this.courseId = courseId;
        this.personId = personId;
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

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getPersonLastName() {
        return personLastName;
    }

    public void setPersonLastName(String personLastName) {
        this.personLastName = personLastName;
    }

    public String getPersonFirstName() {
        return personFirstName;
    }

    public void setPersonFirstName(String personFirstName) {
        this.personFirstName = personFirstName;
    }
}
