package org.javatraining.model;

import org.javatraining.entity.enums.CourseStatus;

/**
 * Created by olga on 06.07.15.
 */
public class CourseWithStatusVO extends CourseVO {
    private CourseStatus courseStatus;

    public CourseWithStatusVO() {
        super();
    }

    public CourseStatus getCourseStatus() {
        return courseStatus;
    }

    public void setCourseStatus(CourseStatus courseStatus) {
        this.courseStatus = courseStatus;
    }
}
