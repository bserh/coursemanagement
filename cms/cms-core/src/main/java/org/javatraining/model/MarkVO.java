package org.javatraining.model;


import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by vika on 30.05.15.
 */
public class MarkVO implements Serializable {
    private Long id;
    @NotNull
    private Integer mark;
    private Long lessonId;

    public MarkVO() {
    }

    public MarkVO(Integer mark) {
        this.mark = mark;
    }

    public MarkVO(Long id, Integer mark) {
        this.id = id;
        this.mark = mark;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMark() {
        return mark;
    }

    public void setMark(Integer mark) {
        this.mark = mark;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MarkVO markVO = (MarkVO) o;

        if (id != null ? !id.equals(markVO.id) : markVO.id != null) return false;
        return !(mark != null ? !mark.equals(markVO.mark) : markVO.mark != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (mark != null ? mark.hashCode() : 0);
        return result;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }
}
