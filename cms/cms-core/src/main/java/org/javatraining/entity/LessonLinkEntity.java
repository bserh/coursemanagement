package org.javatraining.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@NamedQueries({
        @NamedQuery(name = "LessonLinkEntity.findAllLessonLinks", query = "SELECT c FROM LessonLinkEntity c"),
        @NamedQuery(name = "LessonLinkEntity.findAllLessonLinksByLesson", query = "SELECT l FROM LessonLinkEntity l WHERE l.lesson.id = :lesson_id"),
        @NamedQuery(name = "LessonLinkEntity.findAllLessonLinksByLessonAndOrderNum", query = "SELECT l FROM LessonLinkEntity l WHERE l.lesson.id = :lesson_id AND l.orderNum = :order_num")
})
@Table(name = "lesson_link", schema = "")
public class LessonLinkEntity implements Serializable, GenericEntity {
    public static String FIND_ALL_LESSON_LINKS = "LessonLinkEntity.findAllLessonLinks";
    public static String FIND_ALL_LESSON_LINKS_BY_LESSON = "LessonLinkEntity.findAllLessonLinksByLesson";
    public static String FIND_ALL_LESSON_LINKS_BY_LESSON_AND_ORDER_NUM = "LessonLinkEntity.findAllLessonLinksByLessonAndOrderNum";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    private Long id;

    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic
    @Column(name = "order_num", nullable = false, insertable = true, updatable = true)
    private Long orderNum;

    @NotNull
    @Basic
    @Column(name = "description", nullable = false, insertable = true, updatable = true, length = 255)
    private String description;

    @NotNull
    @Basic
    @Column(name = "link", nullable = false, insertable = true, updatable = true, length = 255)
    private String link;

    @ManyToOne(cascade = {})
    @JoinColumn(name = "lesson_id", nullable = false)
    private LessonEntity lesson;

    public LessonLinkEntity() {
    }

    public LessonLinkEntity(String description, String link, LessonEntity lesson) {
        this.description = description;
        this.link = link;
        this.lesson = lesson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Long orderNum) {
        this.orderNum = orderNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public LessonEntity getLesson() {
        return lesson;
    }

    public void setLesson(LessonEntity lesson) {
        this.lesson = lesson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LessonLinkEntity)) return false;

        LessonLinkEntity that = (LessonLinkEntity) o;

        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (orderNum != null ? !orderNum.equals(that.orderNum) : that.orderNum != null) return false;
        if (lesson != null ? !lesson.equals(that.lesson) : that.lesson != null) return false;
        if (link != null ? !link.equals(that.link) : that.link != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (orderNum != null ? orderNum.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + (lesson != null ? lesson.hashCode() : 0);
        return result;
    }
}
