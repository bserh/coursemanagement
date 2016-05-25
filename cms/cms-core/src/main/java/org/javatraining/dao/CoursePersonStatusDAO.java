package org.javatraining.dao;

import org.javatraining.dao.exception.EntityIsAlreadyExistException;
import org.javatraining.entity.CoursePersonStatusEntity;

import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.List;

@Stateless
public class CoursePersonStatusDAO extends GenericDAO<CoursePersonStatusEntity> {
    public CoursePersonStatusDAO() {
        setEntityClass(CoursePersonStatusEntity.class);
    }

    @Override
    public CoursePersonStatusEntity save(@NotNull CoursePersonStatusEntity entity) {
        if (getStatusByPersonIdAndCourseId(entity.getPerson().getId(), entity.getCourse().getId()) == null) {
            return super.save(entity);
        }
        throw new EntityIsAlreadyExistException(String.format("Status for %s and %s is already set",
                entity.getPerson(), entity.getCourse()));
    }

    public List<CoursePersonStatusEntity> getCourseStatusesForPerson(@NotNull Long personId) {
        TypedQuery<CoursePersonStatusEntity> query = getEntityManager().createQuery(
                "SELECT status FROM CoursePersonStatusEntity status WHERE status.person.id = :personId",
                CoursePersonStatusEntity.class);
        query.setParameter("personId", personId);
        return query.getResultList();
    }

    @Nullable
    public CoursePersonStatusEntity getStatusByPersonIdAndCourseId(@NotNull Long personId, @NotNull Long courseId) {
        TypedQuery<CoursePersonStatusEntity> query = getEntityManager().createQuery(
                "SELECT status FROM CoursePersonStatusEntity status " +
                        "WHERE status.person.id = :personId AND status.course.id = :courseId",
                CoursePersonStatusEntity.class);
        query.setParameter("personId", personId);
        query.setParameter("courseId", courseId);
        List<CoursePersonStatusEntity> statusEntityList = query.getResultList();
        if (statusEntityList.size() == 0) {
            return null;
        }
        if (statusEntityList.size() > 1) {
            throw new EntityIsAlreadyExistException("Multiple entities with unique person-course pair");
        }
        return statusEntityList.get(0);
    }
}
