package org.javatraining.dao;


import org.javatraining.entity.LessonEntity;
import org.javatraining.entity.PracticeLessonEntity;

import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.List;


@Stateless
public class PracticeLessonDAO extends GenericDAO<PracticeLessonEntity> {

    public PracticeLessonDAO() {
        setEntityClass(PracticeLessonEntity.class);
    }

    public List<PracticeLessonEntity> getAllPracticeLesson() {
        Query query = getEntityManager().createQuery("SELECT c FROM PracticeLessonEntity c");
        return query.getResultList();
    }

    public List<PracticeLessonEntity> getPracticesForLesson(@NotNull LessonEntity lessonEntity) {
        TypedQuery<PracticeLessonEntity> query = getEntityManager().createQuery(
                "SELECT pr FROM PracticeLessonEntity pr WHERE pr.lesson = :lesson", PracticeLessonEntity.class);
        query.setParameter("lesson", lessonEntity);
        return query.getResultList();
    }
    public List<PracticeLessonEntity> getPracticesForCourse(@NotNull Long courseId) {
        TypedQuery<PracticeLessonEntity> query = getEntityManager().createQuery(
                "SELECT pr FROM PracticeLessonEntity pr WHERE pr.lesson.course.id = :courseId", PracticeLessonEntity.class);
        query.setParameter("courseId", courseId);
        return query.getResultList();
    }
}
