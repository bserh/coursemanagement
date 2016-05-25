package org.javatraining.model;

import java.util.List;

/**
 * Created by olga on 27.06.15.
 */
public class CourseWithDetailsVO extends CourseVO {
    private List<PersonVO> teachers;

    public CourseWithDetailsVO() {
        super();
    }

    public List<PersonVO> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<PersonVO> teachers) {
        this.teachers = teachers;
    }
}
