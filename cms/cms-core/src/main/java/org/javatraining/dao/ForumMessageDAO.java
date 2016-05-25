package org.javatraining.dao;


import org.javatraining.entity.ForumMessageEntity;
import org.javatraining.entity.LessonEntity;
import org.javatraining.entity.PersonEntity;

import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;
import java.util.List;


@Stateless
public class ForumMessageDAO extends GenericDAO<ForumMessageEntity> {

    public ForumMessageDAO() {
        setEntityClass(ForumMessageEntity.class);
    }

    public ForumMessageEntity save(@NotNull ForumMessageEntity forumMessagesEntity, @NotNull PersonEntity personEntity, @NotNull LessonEntity lesson) {
        forumMessagesEntity.setPersons(personEntity);
        forumMessagesEntity.setLessons(lesson);
        getEntityManager().persist(forumMessagesEntity);
        return forumMessagesEntity;
    }

    public List<ForumMessageEntity> getAllForumMassage() {
        Query query = getEntityManager().createQuery("SELECT c FROM ForumMassagesEntity c");
        return query.getResultList();
    }
}
