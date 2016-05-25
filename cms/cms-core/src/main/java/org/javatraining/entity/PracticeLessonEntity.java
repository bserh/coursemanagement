package org.javatraining.entity;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "practice_lesson", schema = "")
public class PracticeLessonEntity implements Serializable, GenericEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    private Long id;
    @NotNull
    @Basic
    @Column(name = "task", nullable = false, insertable = true, updatable = true, length = 255)
    private String task;

    @Column(name = "orderNum", nullable = true)
    private Long orderNum;

    @ManyToOne(cascade = {})
    @JoinColumn(name = "lesson_id", nullable = false, insertable = true, updatable = true)
    private LessonEntity lesson;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "lessons")
    private Set<MarkEntity> marks;

    public PracticeLessonEntity() {
    }

    public PracticeLessonEntity(String task, LessonEntity lesson) {
        this.task = task;
        this.lesson = lesson;
    }

    public PracticeLessonEntity(Long id, String task, Long orderNum) {
        this.id = id;
        this.task = task;
        this.orderNum = orderNum;
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

    public Long getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Long orderNum) {
        this.orderNum = orderNum;
    }


    public LessonEntity getLesson() {
        return lesson;
    }

    public void setLesson(LessonEntity lesson) {
        this.lesson = lesson;
    }

    public Set<MarkEntity> getMarks() {
        return marks;
    }

    public void setMarks(Set<MarkEntity> marks) {
        this.marks = marks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PracticeLessonEntity)) return false;
        PracticeLessonEntity that = (PracticeLessonEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (lesson != null ? !lesson.equals(that.lesson) : that.lesson != null) return false;
        if (task != null ? !task.equals(that.task) : that.task != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (task != null ? task.hashCode() : 0);
        result = 31 * result + (lesson != null ? lesson.hashCode() : 0);
        return result;
    }
}