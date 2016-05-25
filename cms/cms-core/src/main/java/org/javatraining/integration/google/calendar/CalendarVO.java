package org.javatraining.integration.google.calendar;

import org.javatraining.model.PersonVO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class CalendarVO {
    private String id;
    @NotNull
    private String title;
    @NotNull
    @Valid
    private List<PersonVO> teachers;
    @Valid
    private List<PersonVO> students;

    public CalendarVO() {

    }

    public CalendarVO(String title, List<PersonVO> teachers) {
        this.title = title;
        this.teachers = teachers;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public List<PersonVO> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<PersonVO> teachers) {
        this.teachers = teachers;
    }

    public List<PersonVO> getStudents() {
        return students;
    }

    public void setStudents(List<PersonVO> students) {
        this.students = students;
    }

    @Override
    public String toString() {
        return "CalendarVO{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", teachers=" + teachers +
                ", students=" + students +
                '}';
    }
}
