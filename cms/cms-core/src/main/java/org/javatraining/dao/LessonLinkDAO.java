package org.javatraining.dao;


import org.javatraining.entity.LessonEntity;
import org.javatraining.entity.LessonLinkEntity;

import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;
import java.util.List;

@Stateless
public class LessonLinkDAO extends GenericDAO<LessonLinkEntity> {

    public LessonLinkDAO() {
        setEntityClass(LessonLinkEntity.class);
    }

    public LessonLinkEntity save(@NotNull LessonLinkEntity lessonLinks, @NotNull LessonEntity lesson) {
        lessonLinks.setLesson(lesson);
        getEntityManager().persist(lessonLinks);
        return lessonLinks;
    }

    public List<LessonLinkEntity> getAllLessonLinks() {
        Query query = getEntityManager().createNamedQuery(LessonLinkEntity.FIND_ALL_LESSON_LINKS);
        return query.getResultList();
    }

    public List<LessonLinkEntity> getAllLessonLinksByLesson(LessonEntity lesson) {
        return (List<LessonLinkEntity>) getEntityManager().createNamedQuery(LessonLinkEntity.FIND_ALL_LESSON_LINKS_BY_LESSON)
                .setParameter("lesson_id", lesson.getId())
                .getResultList();
    }

    public LessonLinkEntity getByOrderNum(LessonEntity lesson, Long orderNum) {
        Query query = getEntityManager().createNamedQuery(LessonLinkEntity.FIND_ALL_LESSON_LINKS_BY_LESSON_AND_ORDER_NUM)
                .setParameter("lesson_id", lesson.getId())
                .setParameter("order_num", orderNum);
        return (LessonLinkEntity) query.getSingleResult();
    }

    public LessonLinkEntity updateByOrderNum(LessonEntity lesson, Long orderNum, LessonLinkEntity lessonLink) {
        LessonLinkEntity lessonLinkEntity = getByOrderNum(lesson, orderNum);
        lessonLinkEntity.setOrderNum(lessonLink.getOrderNum());
        lessonLinkEntity.setDescription(lessonLink.getDescription());
        lessonLinkEntity.setLink(lessonLink.getLink());

        return lessonLinkEntity;
    }

    public void deleteByOrderNum(LessonEntity lesson, Long orderNum) {
        LessonLinkEntity lessonLinkEntity = getByOrderNum(lesson, orderNum);
        getEntityManager().remove(lessonLinkEntity);
    }
}
