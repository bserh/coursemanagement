package org.javatraining.dao;

import org.javatraining.dao.exception.EntityIsAlreadyExistException;
import org.javatraining.dao.exception.EntityNotExistException;
import org.javatraining.entity.LessonEntity;
import org.javatraining.entity.LessonLinkEntity;
import org.javatraining.entity.util.Pair;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.*;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ejb.EJBException;

import static org.assertj.core.api.StrictAssertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by vika on 12.06.15.
 */

@RunWith(Arquillian.class)
@Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
@UsingDataSet(value = "dao-tests/lesson-link/lesson-link.json")
public class LessonLinkDAOTest {
    @EJB
    LessonLinkDAO lessonLinkDAO;

    @EJB
    LessonDAO lessonDAO;


    private static final String DS_DIR = "dao-tests/";
    private static final String DS_EMPTY = DS_DIR + "empty.json";

    private static final String DS_LESSON_LINK = DS_DIR + "lesson-link/lesson-link.json";
    private static final String DS_LESSON_LINK_AFTER_UPDATE = DS_DIR + "lesson-link/expected-after-update.json";
    private static final String DS_LESSON_LINK_AFTER_SAVE = DS_DIR + "lesson-link/expected-after-save.json";
    private static final String DS_LESSON_LINK_AFTER_REMOVE = DS_DIR + "lesson-link/expected-after-remove.json";

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
    public void testLessonLinkDAOShouldBeInjected() throws Exception {
        assertThat(lessonLinkDAO, is(notNullValue()));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON_LINK_AFTER_SAVE}, excludeColumns = {"id"})
    public void testSaveLesson() {
        lessonLinkDAO.save(lessonLinkEntityInitializationForTests());
    }


    @Test
    public void testSaveReturnLessonEntity() {
        LessonLinkEntity lessonLinkEntity = lessonLinkEntityInitializationForTests();
        assertEquals(lessonLinkDAO.save(lessonLinkEntity), lessonLinkEntity);
    }

    @Test
    public void testUpdateReturnLessonEntity() {
        LessonLinkEntity lessonLinkEntity = predefinedLessonLinkInitialization();
        lessonLinkEntity.setDescription("otherDescription");
        assertEquals(lessonLinkEntity, lessonLinkDAO.update(lessonLinkEntity));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON_LINK_AFTER_UPDATE}, excludeColumns = {"id"})
    public void testUpdateLesson() {
        LessonLinkEntity lessonLinkEntity = predefinedLessonLinkInitialization();
        lessonLinkEntity.setDescription("Other description");
        lessonLinkDAO.update(lessonLinkEntity);
    }

    @Test
    public void testRemoveReturnLessonEntity() {
        LessonLinkEntity lessonLinkEntity = predefinedLessonLinkInitialization();
        assertEquals(lessonLinkDAO.remove(lessonLinkEntity), lessonLinkEntity);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_LESSON_LINK_AFTER_REMOVE})
    public void testRemoveLessonLink() {
        lessonLinkDAO.remove(predefinedLessonLinkInitialization());
    }

    @Test
    @ShouldMatchDataSet(value = {DS_LESSON_LINK_AFTER_REMOVE})
    public void testRemoveByIdLessonLink() {
        lessonLinkDAO.removeById(predefinedLessonLinkInitialization().getId());
    }


    @Test
    public void testGetReturnLessonLinkEntity() {
        LessonLinkEntity lessonLinkForGet = predefinedLessonLinkInitialization();
        assertThat(lessonLinkForGet, is(equalTo(lessonLinkDAO.getById(lessonLinkForGet.getId()))));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON_LINK})
    public void testGetLessonLink() {
        assertNotNull(lessonLinkDAO.getById(predefinedLessonLinkInitialization().getId()));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON_LINK})
    public void testSaveNullLessonLinkTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> lessonLinkDAO.save(null))
                .isInstanceOf(EJBException.class);

    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON_LINK})
    public void testRemoveNullLessonLinkTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> lessonLinkDAO.remove(null))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON_LINK})
    public void testUpdateNullLessonLinkTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> lessonLinkDAO.update(null))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON_LINK})
    public void testSaveNotValidLessonLinkTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> lessonLinkDAO.save(new LessonLinkEntity()))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON_LINK})
    public void testUpdateNotValidLessonLinkTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> lessonLinkDAO.update(new LessonLinkEntity()))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON_LINK})
    public void testRemoveNotValidLessonLinkTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> lessonLinkDAO.remove(new LessonLinkEntity()))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON_LINK})
    public void testGetByIdForNotExistingIdShouldReturnEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        assertThatThrownBy(() -> lessonLinkDAO.getById(notExistingId))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with " + notExistingId + " does not exist in database");

    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON_LINK})
    public void testRemoveByIdForNotExistingIdShouldReturnEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        assertThatThrownBy(() -> lessonLinkDAO.removeById(notExistingId))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON_LINK})
    public void testSaveLessonLinkThatExistThrowsEntityIsAlreadyExistException() throws EntityIsAlreadyExistException {
        LessonLinkEntity lessonLinkThatExists = predefinedLessonLinkInitialization();
        assertThatThrownBy(() -> lessonLinkDAO.save(lessonLinkThatExists))
                .isInstanceOf(EntityIsAlreadyExistException.class).hasMessage("Field with id = "
                + lessonLinkThatExists.getId() + " already exists in database");
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON_LINK})
    public void testUpdateLessonLinkThatNotExistThrowsEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        LessonLinkEntity lessonLinkThatNotExists = predefinedLessonLinkInitialization();
        lessonLinkThatNotExists.setId(notExistingId);
        assertThatThrownBy(() -> lessonLinkDAO.update(lessonLinkThatNotExists))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON_LINK})
    public void testRemoveLessonLinkThatNotExistThrowsEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        LessonLinkEntity lessonLinkThatNotExists = lessonLinkEntityInitializationForTests();
        lessonLinkThatNotExists.setId(notExistingId);
        assertThatThrownBy(() -> lessonLinkDAO.remove(lessonLinkThatNotExists))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }


    private LessonLinkEntity lessonLinkEntityInitializationForTests() {
        Long predefinedLessonId = 1L;
        LessonLinkEntity lessonLinkEntity = new LessonLinkEntity();
        LessonEntity lessonEntity = lessonDAO.getById(predefinedLessonId);
        lessonLinkEntity.setDescription("description");
        lessonLinkEntity.setOrderNum(3L);
        lessonLinkEntity.setLink("otherLink");
        lessonLinkEntity.setLesson(lessonEntity);
        return lessonLinkEntity;
    }

    private LessonLinkEntity predefinedLessonLinkInitialization() {
        LessonLinkEntity lessonLinkEntity = new LessonLinkEntity();
        Long predefinedLessonId = 1L;
        Long predefinedLessonLinkId = 1L;
        LessonEntity lessonEntity = lessonDAO.getById(predefinedLessonId);
        lessonLinkEntity.setDescription("description");
        lessonLinkEntity.setOrderNum(3L);
        lessonLinkEntity.setLink("someLink");
        lessonLinkEntity.setLesson(lessonEntity);
        lessonLinkEntity.setId(predefinedLessonLinkId);
        return lessonLinkEntity;
    }

}
