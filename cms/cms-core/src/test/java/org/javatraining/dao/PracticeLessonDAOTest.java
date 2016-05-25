package org.javatraining.dao;

import org.javatraining.dao.exception.EntityIsAlreadyExistException;
import org.javatraining.dao.exception.EntityNotExistException;
import org.javatraining.entity.LessonEntity;
import org.javatraining.entity.PracticeLessonEntity;
import org.javatraining.entity.util.Pair;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.*;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
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
@UsingDataSet(value = "dao-tests/practice-lesson/practice-lesson.json")
public class PracticeLessonDAOTest {
    @EJB
    PracticeLessonDAO practiceLessonDAO;

    @EJB
    LessonDAO lessonDAO;

    private static final String DS_DIR = "dao-tests/";
    private static final String DS_EMPTY = DS_DIR + "empty.json";

    private static final String DS_PRACTICE_LESSON = DS_DIR + "practice-lesson/practice-lesson.json";
    private static final String DS_PRACTICE_LESSON_AFTER_UPDATE = DS_DIR + "practice-lesson/expected-after-update.json";
    private static final String DS_PRACTICE_LESSON_AFTER_SAVE = DS_DIR + "practice-lesson/expected-after-save.json";
    private static final String DS_PRACTICE_LESSON_AFTER_REMOVE = DS_DIR + "practice-lesson/expected-after-remove.json";


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
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        return war;
    }

    @Test
    public void testPracticeLessonDAOShouldBeInjected() throws Exception {
        assertThat(practiceLessonDAO, is(notNullValue()));
    }


    public void testRemoveReturnPracticeLessonEntity() {
        PracticeLessonEntity predefinedPracticeLesson = predefinedPracticeLessonInitialization();
        assertEquals(predefinedPracticeLesson, practiceLessonDAO.remove(predefinedPracticeLesson));
    }


    @Test
    public void testSaveReturnPracticeLessonEntity() {
        PracticeLessonEntity practiceLessonForSave = practiceLessonInitialisationForTests();
        assertEquals(practiceLessonForSave, practiceLessonDAO.save(practiceLessonForSave));
    }


    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PRACTICE_LESSON_AFTER_SAVE}, excludeColumns = {"id"})
    public void testSavePracticeLesson() {
        practiceLessonDAO.save(practiceLessonInitialisationForTests());
    }

    @Test
    public void testUpdateReturnPracticeLessonEntity() {
        PracticeLessonEntity practiceLessonEntity = predefinedPracticeLessonInitialization();
        practiceLessonEntity.setTask("otherTask");
        practiceLessonDAO.update(practiceLessonEntity);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PRACTICE_LESSON_AFTER_UPDATE}, excludeColumns = {"id"})
    public void testUpdatePracticeLesson() {
        PracticeLessonEntity practiceLessonForUpdate = predefinedPracticeLessonInitialization();
        practiceLessonForUpdate.setTask("other task");
        practiceLessonDAO.update(practiceLessonForUpdate);
    }

    @Test
    public void testGetReturnPracticeLessonEntity() {
        PracticeLessonEntity practiceLessonForGet = predefinedPracticeLessonInitialization();
        assertThat(practiceLessonForGet, is(equalTo(practiceLessonDAO.getById(practiceLessonForGet.getId()))));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PRACTICE_LESSON})
    public void testGetLessonLink() {
        assertNotNull(practiceLessonDAO.getById(predefinedPracticeLessonInitialization().getId()));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PRACTICE_LESSON})
    public void testGetByIdForNotExistingIdShouldReturnEntityDoesNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        assertThatThrownBy(() -> practiceLessonDAO.getById(notExistingId))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with " + notExistingId + " does not exist in database");

    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PRACTICE_LESSON})
    public void testRemoveByIdForNotExistingIdShouldReturnEntityDoesNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        assertThatThrownBy(() -> practiceLessonDAO.removeById(notExistingId))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PRACTICE_LESSON})
    public void testSaveNullPracticeLessonTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> practiceLessonDAO.save(null))
                .isInstanceOf(EJBException.class);

    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PRACTICE_LESSON})
    public void testRemoveNullPracticeLessonTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> practiceLessonDAO.remove(null))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PRACTICE_LESSON})
    public void testUpdateNullPracticeLessonTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> practiceLessonDAO.update(null))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PRACTICE_LESSON})
    public void testSaveNotValidPracticeLessonTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> practiceLessonDAO.save(new PracticeLessonEntity()))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PRACTICE_LESSON})
    public void testUpdateNotValidPracticeLessonTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> practiceLessonDAO.update(new PracticeLessonEntity()))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PRACTICE_LESSON})
    public void testRemoveNotValidPracticeLessonTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> practiceLessonDAO.remove(new PracticeLessonEntity()))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PRACTICE_LESSON})
    public void testSavePracticeLessonThatExistThrowsEntityIsAlreadyExistException() throws EntityIsAlreadyExistException {
        PracticeLessonEntity practiceLessonThatExists = predefinedPracticeLessonInitialization();
        assertThatThrownBy(() -> practiceLessonDAO.save(practiceLessonThatExists))
                .isInstanceOf(EntityIsAlreadyExistException.class).hasMessage("Field with id = "
                + practiceLessonThatExists.getId() + " already exists in database");
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PRACTICE_LESSON})
    public void testUpdatePracticeLessonThatNotExistThrowsEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        PracticeLessonEntity practiceLessonThatNotExists = practiceLessonInitialisationForTests();
        practiceLessonThatNotExists.setId(notExistingId);
        assertThatThrownBy(() -> practiceLessonDAO.update(practiceLessonThatNotExists))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PRACTICE_LESSON})
    public void testRemovePracticeLessonThatNotExistThrowsEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        PracticeLessonEntity practiceLessonThatNotExists = practiceLessonInitialisationForTests();
        practiceLessonThatNotExists.setId(notExistingId);
        assertThatThrownBy(() -> practiceLessonDAO.remove(practiceLessonThatNotExists))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }


    private PracticeLessonEntity practiceLessonInitialisationForTests() {
        Long predefinedLessonId = 1L;
        PracticeLessonEntity practiceLessonEntity = new PracticeLessonEntity();
        LessonEntity predefinedLesson = lessonDAO.getById(predefinedLessonId);
        practiceLessonEntity.setTask("otherPracticeTask");
        practiceLessonEntity.setLesson(predefinedLesson);
        return practiceLessonEntity;
    }

    private PracticeLessonEntity predefinedPracticeLessonInitialization() {
        Long predefinedLessonId = 1L;
        Long predefinedPracticeLessonId = 1L;
        PracticeLessonEntity practiceLessonEntity = new PracticeLessonEntity();
        practiceLessonEntity.setId(predefinedPracticeLessonId);
        LessonEntity predefinedLesson = lessonDAO.getById(predefinedLessonId);
        practiceLessonEntity.setTask("practiceTask");
        practiceLessonEntity.setLesson(predefinedLesson);
        return practiceLessonEntity;
    }


}
