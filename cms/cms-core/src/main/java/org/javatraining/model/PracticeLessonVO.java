package org.javatraining.model;

import javax.validation.constraints.NotNull;

/**
 * Created by olga on 18.06.15.
 */
public class PracticeLessonVO {
    private Long id;
    @NotNull
    private String task;
    @NotNull
    private Long orderNum;
    private Long lessonOrderNum;

    public PracticeLessonVO() {
    }

    public PracticeLessonVO(Long id, String task, Long orderNum) {
        this.task = task;
        this.id = id;
        this.orderNum = orderNum;
    }

    public PracticeLessonVO(Long id, String task) {
        this.task = task;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PracticeLessonVO that = (PracticeLessonVO) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return !(task != null ? !task.equals(that.task) : that.task != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (task != null ? task.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PracticeLessonVO{" +
                "id=" + id +
                ", task='" + task + '\'' +
                '}';
    }

    public Long getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Long orderNum) {
        this.orderNum = orderNum;
    }

    public Long getLessonOrderNum() {
        return lessonOrderNum;
    }

    public void setLessonOrderNum(Long lessonOrderNum) {
        this.lessonOrderNum = lessonOrderNum;
    }
}
