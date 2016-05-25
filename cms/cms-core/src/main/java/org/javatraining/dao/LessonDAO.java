package org.javatraining.dao;


import org.javatraining.entity.LessonEntity;
import org.javatraining.entity.PracticeLessonEntity;
import org.javatraining.entity.util.Pair;

import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Stateless
public class LessonDAO extends GenericDAO<LessonEntity> {

    public LessonDAO() {
        setEntityClass(LessonEntity.class);
    }

    public List<LessonEntity> getAllLessons() {
        Query query = getEntityManager().createQuery("SELECT c FROM LessonEntity c");
        return query.getResultList();
    }

    public LessonEntity getByOrderNum(Long courseId, Long orderNum) {
        return (LessonEntity) getEntityManager().createNamedQuery(LessonEntity.FIND_BY_COURSE_AND_ORDER_NUM)
                .setParameter("course_id", courseId)
                .setParameter("order_num", orderNum)
                .getSingleResult();
    }

    public List<LessonEntity> getByCourseId(Long courseId) {
        return (List<LessonEntity>) getEntityManager().createNamedQuery(LessonEntity.FIND_BY_COURSE)
                .setParameter("course_id", courseId)
                .getResultList();
    }

    public LessonEntity updateByOrderNum(Long courseId, Long orderNum, LessonEntity lesson) {
        LessonEntity lessonEntity = getByOrderNum(courseId, orderNum);
        lessonEntity.setTopic(lesson.getTopic());
        lessonEntity.setCreateDate(lesson.getCreateDate());
        lessonEntity.setDescription(lesson.getDescription());
        lessonEntity.setOrderNum(lessonEntity.getOrderNum());

        return lessonEntity;
    }

    public void removeByOrderNum(Long courseId, Long orderNum) {
        LessonEntity lessonEntity = getByOrderNum(courseId, orderNum);
        getEntityManager().remove(lessonEntity);
    }

    public List<Pair<LessonEntity, List<PracticeLessonEntity>>> getWithPracticesByCourseId(@NotNull Long courseId) {
        TypedQuery<Object[]> query = getEntityManager().createQuery(
                "SELECT lesson, practice FROM LessonEntity lesson LEFT JOIN lesson.practiceLesson practice WHERE lesson.course.id = :courseId",
                Object[].class).setParameter("courseId", courseId);
        List<Object[]> resultList = query.getResultList();
        return Stream
                .concat(resultList.stream()
                                .filter(ar -> (ar[1] != null))
                                .map((ar) -> (PracticeLessonEntity) ar[1])
                                .collect(Collectors.groupingBy(PracticeLessonEntity::getLesson))
                                .entrySet().stream()
                                .map((entry) -> new Pair<>(entry.getKey(), entry.getValue())),
                        resultList.stream()
                                .filter(ar -> (ar[1] == null))
                                .map(ar -> new Pair<>((LessonEntity) ar[0], (List<PracticeLessonEntity>) new ArrayList<PracticeLessonEntity>(0))))
                .collect(Collectors.toList());
    }
}
