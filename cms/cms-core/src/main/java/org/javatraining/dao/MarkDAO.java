package org.javatraining.dao;


import org.javatraining.entity.MarkEntity;
import org.javatraining.entity.PersonEntity;
import org.javatraining.entity.PracticeLessonEntity;

import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;
import java.util.List;

@Stateless
public class MarkDAO extends GenericDAO<MarkEntity> {

    public MarkDAO() {
        setEntityClass(MarkEntity.class);
    }

    public MarkEntity save(@NotNull MarkEntity mark, @NotNull PersonEntity person, @NotNull PracticeLessonEntity lessons) {
        mark.setPersons(person);
        mark.setPracticeLesson(lessons);
        MarkEntity savedEntity = super.save(mark);
        return savedEntity;
    }

    public List<MarkEntity> getAllMarks() {
        Query query = getEntityManager().createQuery("SELECT c FROM MarkEntity c");
        return query.getResultList();
    }
}
