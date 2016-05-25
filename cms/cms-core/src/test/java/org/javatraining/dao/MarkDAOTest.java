package org.javatraining.dao;

import org.javatraining.dao.exception.EntityIsAlreadyExistException;
import org.javatraining.dao.exception.EntityNotExistException;
import org.javatraining.entity.MarkEntity;
import org.javatraining.entity.PersonEntity;
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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;

/**
 * Created by vika on 12.06.15.
 */

@RunWith(Arquillian.class)
@Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
@UsingDataSet(value = "dao-tests/mark/mark.json")
public class MarkDAOTest {
    @EJB
    MarkDAO markDAO;

    @EJB
    PersonDAO personDAO;

    @EJB
    PracticeLessonDAO practiceLessonDAO;

    private static final String DS_DIR = "dao-tests/";
    private static final String DS_EMPTY = DS_DIR + "empty.json";

    private static final String DS_MARK = DS_DIR + "mark/mark.json";
    private static final String DS_MARK_AFTER_UPDATE = DS_DIR + "mark/expected-after-update.json";
    private static final String DS_MARK_AFTER_SAVE = DS_DIR + "mark/expected-after-save.json";
    private static final String DS_MARK_AFTER_REMOVE = DS_DIR + "mark/expected-after-remove.json";


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
    public void testMarkDAOShouldBeInjected() throws Exception {
        assertThat(markDAO, is(notNullValue()));
    }

    @Test
    public void testSaveReturnMarkEntity() {
        MarkEntity markForSave = markInitializationForTests();
        assertEquals(markForSave, markDAO.save(markForSave));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_MARK_AFTER_SAVE})
    public void testSaveMark() {
        markDAO.save(markInitializationForTests());
    }

    @Test
    @ShouldMatchDataSet(value = {DS_MARK_AFTER_UPDATE})
    public void testUpdateMark() {
        MarkEntity markForUpdate = predefinedMarkInitialization();
        markForUpdate.setMark(85L);
        markDAO.update(markForUpdate);
    }

    @Test
    public void testUpdateReturnMarkEntity() {
        MarkEntity markForUpdate = predefinedMarkInitialization();
        markForUpdate.setMark(85L);
        assertEquals(markDAO.update(markForUpdate), markForUpdate);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_MARK_AFTER_REMOVE})
    public void testRemoveMark() {
        markDAO.remove(predefinedMarkInitialization());
    }

    @Test
    public void testRemoveReturnMarkEntity() {
        MarkEntity markForRemove = predefinedMarkInitialization();
        assertEquals(markForRemove, markDAO.remove(markForRemove));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_MARK_AFTER_REMOVE})
    public void testRemoveByIdMark() {
        markDAO.removeById(predefinedMarkInitialization().getId());
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_MARK}, excludeColumns = {"id"})
    public void testGetAllMarks() {
        assertThat(markDAO.getAllMarks(), is(notNullValue()));
    }


    @Test
    public void testGetByIdForNotExistingIdShouldReturnEntityNotExistException() {
        Long notExistingId = 10L;
        assertThatThrownBy(() -> markDAO.getById(notExistingId))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }

    @Test
    public void testRemoveByIdForNotExistingIdShouldReturnEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        assertThatThrownBy(() -> markDAO.removeById(notExistingId))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_MARK})
    public void testSaveNullMarkTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> markDAO.save(null))
                .isInstanceOf(EJBException.class);

    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_MARK})
    public void testRemoveNullMarkTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> markDAO.remove(null))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_MARK})
    public void testUpdateNullMarkTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> markDAO.update(null))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_MARK})
    public void testSaveNotValidMarkTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> markDAO.save(new MarkEntity()))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_MARK})
    public void testUpdateNotValidMarkTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> markDAO.update(new MarkEntity()))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_MARK})
    public void testRemoveNotValidMarkTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> markDAO.remove(new MarkEntity()))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_MARK})
    public void testSaveMarkThatExistThrowsEntityIsAlreadyExistException() throws EntityIsAlreadyExistException {
        MarkEntity markThatExists = predefinedMarkInitialization();
        assertThatThrownBy(() -> markDAO.save(markThatExists))
                .isInstanceOf(EntityIsAlreadyExistException.class).hasMessage("Field with id = "
                + markThatExists.getId() + " already exists in database");
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_MARK})
    public void testUpdateMarkThatNotExistThrowsEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        MarkEntity markThatExists = predefinedMarkInitialization();
        markThatExists.setId(notExistingId);
        assertThatThrownBy(() -> markDAO.update(markThatExists))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_MARK})
    public void testRemoveMarkThatNotExistThrowsEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        MarkEntity markThatExists = predefinedMarkInitialization();
        markThatExists.setId(notExistingId);
        assertThatThrownBy(() -> markDAO.remove(markThatExists))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }

    private MarkEntity markInitializationForTests() {
        MarkEntity markEntity = new MarkEntity();
        Long predefinedPersonId = 1L;
        Long predefinedPracticeLessonId = 1L;
        PersonEntity predefinedPerson = personDAO.getById(predefinedPersonId);
        PracticeLessonEntity predefinedLesson = practiceLessonDAO.getById(predefinedPracticeLessonId);
        markEntity.setPracticeLesson(predefinedLesson);
        markEntity.setPersons(predefinedPerson);
        markEntity.setMark(85L);

        return markEntity;
    }

    private MarkEntity predefinedMarkInitialization() {
        MarkEntity markEntity = new MarkEntity();
        Long predefinedPersonId = 1L;
        Long predefinedMarkId = 1L;
        Long predefinedPracticeLessonId = 1L;
        PersonEntity predefinedPerson = personDAO.getById(predefinedPersonId);
        PracticeLessonEntity predefinedLesson = practiceLessonDAO.getById(predefinedPracticeLessonId);
        markEntity.setPracticeLesson(predefinedLesson);
        markEntity.setPersons(predefinedPerson);
        markEntity.setId(predefinedMarkId);
        markEntity.setMark(80L);
        return markEntity;
    }
}

