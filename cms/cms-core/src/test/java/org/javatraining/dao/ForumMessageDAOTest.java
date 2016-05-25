package org.javatraining.dao;

import org.javatraining.dao.exception.EntityIsAlreadyExistException;
import org.javatraining.dao.exception.EntityNotExistException;
import org.javatraining.entity.ForumMessageEntity;
import org.javatraining.entity.LessonEntity;
import org.javatraining.entity.PersonEntity;
import org.javatraining.entity.util.Pair;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.*;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.sql.Timestamp;

import static org.assertj.core.api.StrictAssertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;

/**
 * Created by vika on 12.06.15.
 */

@RunWith(Arquillian.class)
@Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
@UsingDataSet(value = "dao-tests/forumMessage/forumMessage.json")
public class ForumMessageDAOTest {

    @EJB
    ForumMessageDAO forumMessageDAO;

    @EJB
    PersonDAO personDAO;

    @EJB
    LessonDAO lessonDAO;

    private static final String DS_DIR = "dao-tests/";
    private static final String DS_EMPTY = DS_DIR + "empty.json";

    private static final String DS_FORUM_MESSAGE = DS_DIR + "forumMessage/forumMessage.json";

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class)
                .addPackage("org.javatraining.dao")
                .addPackage("org.javatraining.entity")
                .addPackage("org.javatraining.entity.enums")
                .addPackage("org.javatraining.dao.exception")
                .addPackage("org.assertj.core.api")
                .addPackage("org.assertj.core.error")
                .addPackage("org.assertj.core.util.introspection")
                .addPackage("org.assertj.core.util")
                .addPackage("org.assertj.core.presentation")
                .addPackage("org.assertj.core.internal")
                .addClass(Pair.class)
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml");
        return war;
    }

    @Test
    public void testForumMessageDAOShouldBeInjected() throws Exception {
        assertThat(forumMessageDAO, is(notNullValue()));
    }

    @Test
    public void testGetByIdForNotExistingIdShouldReturnEntityNotExistException() {
        Long notExistingId = 10L;
        assertThatThrownBy(() -> forumMessageDAO.getById(notExistingId))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }

    @Test
    public void testRemoveByIdForNotExistingIdShouldReturnEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        assertThatThrownBy(() -> forumMessageDAO.removeById(notExistingId))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }


    @Test
    public void testUpdateReturnPracticeLessonEntity() {
        ForumMessageEntity forumMessagesForUpdate = predefinedForumMessagesInitialization();
        forumMessagesForUpdate.setTitle("Other title");
        assertEquals(forumMessagesForUpdate, forumMessageDAO.update(forumMessagesForUpdate));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_FORUM_MESSAGE})
    public void testSaveForumMessageThatExistThrowsEntityIsAlreadyExistException() throws EntityIsAlreadyExistException {
        ForumMessageEntity forumMessagesThatExists = predefinedForumMessagesInitialization();
        assertThatThrownBy(() -> forumMessageDAO.save(forumMessagesThatExists))
                .isInstanceOf(EntityIsAlreadyExistException.class).hasMessage("Field with id = "
                + forumMessagesThatExists.getId() + " already exists in database");
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_FORUM_MESSAGE})
    public void testUpdateForumMessageThatNotExistThrowsEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        ForumMessageEntity forumMessagesThatNotExists = predefinedForumMessagesInitialization();
        forumMessagesThatNotExists.setId(notExistingId);
        assertThatThrownBy(() -> forumMessageDAO.update(forumMessagesThatNotExists))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_FORUM_MESSAGE})
    public void testRemovePersonThatNotExistThrowsEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        ForumMessageEntity forumMessagesThatNotExists = predefinedForumMessagesInitialization();
        forumMessagesThatNotExists.setId(notExistingId);
        assertThatThrownBy(() -> forumMessageDAO.remove(forumMessagesThatNotExists))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }


    private ForumMessageEntity predefinedForumMessagesInitialization() {
        ForumMessageEntity predefinedForumMessages = new ForumMessageEntity();
        LessonEntity predefinedLesson = lessonDAO.getById(1L);
        PersonEntity predefinedPerson = personDAO.getById(1L);
        predefinedForumMessages.setLessons(predefinedLesson);
        predefinedForumMessages.setPersons(predefinedPerson);
        predefinedForumMessages.setId(1L);
        predefinedForumMessages.setDescription("description");
        predefinedForumMessages.setParentId(785L);
        predefinedForumMessages.setTitle("title");
        predefinedForumMessages.setDate(Timestamp.valueOf("2011-10-02 12:05:12"));

        return predefinedForumMessages;
    }

}


