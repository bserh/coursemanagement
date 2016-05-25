package org.javatraining.dao;

import org.javatraining.dao.exception.EntityIsAlreadyExistException;
import org.javatraining.dao.exception.EntityNotExistException;
import org.javatraining.entity.CourseEntity;
import org.javatraining.entity.LessonEntity;
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
import java.sql.Date;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by vika on 12.06.15.
 */

@RunWith(Arquillian.class)
@Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
@UsingDataSet(value = "dao-tests/lesson/lesson.json")
public class LessonDAOTest {

    @EJB
    LessonDAO lessonDAO;

    @EJB
    CourseDAO courseDAO;

    private static final String DS_DIR = "dao-tests/";
    private static final String DS_EMPTY = DS_DIR + "empty.json";

    private static final String DS_LESSON = DS_DIR + "lesson/lesson.json";
    private static final String DS_LESSON_AFTER_UPDATE = DS_DIR + "lesson/expected-after-update.json";
    private static final String DS_LESSON_AFTER_SAVE = DS_DIR + "lesson/expected-after-save.json";
    private static final String DS_LESSON_AFTER_REMOVE = DS_DIR + "lesson/expected-after-remove.json";

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
        System.out.println(war.toString(true));
        return war;
    }


    @Test
    public void testLessonDAOShouldBeInjected() {
        assertThat(lessonDAO, is(notNullValue()));
    }

    @Test
    public void testGetByIdForNotExistingIdShouldReturnEntityNotExistException() {
        Long notExistingId = 10L;
        assertThatThrownBy(() -> lessonDAO.getById(notExistingId))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }

    @Test
    public void testRemoveByIdForNotExistingIdShouldReturnEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        assertThatThrownBy(() -> lessonDAO.removeById(notExistingId))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }


    @Test
    public void testSaveReturnLesson() {
        LessonEntity lessonForSave = lessonInitializationForTests();
        assertEquals(lessonForSave, lessonDAO.save(lessonForSave));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON_AFTER_SAVE}, excludeColumns = {"id"})
    public void testSaveLesson() {
        lessonDAO.save(lessonInitializationForTests());
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON_AFTER_UPDATE}, excludeColumns = {"id"})
    public void testUpdateLesson() {
        LessonEntity lessonForUpdate = predefinedLessonInitialization();
        lessonForUpdate.setDescription("Other description");
        lessonDAO.update(lessonForUpdate);
    }

    @Test
    public void testUpdateReturnLessonEntity() {
        LessonEntity lessonForUpdate = predefinedLessonInitialization();
        lessonForUpdate.setDescription("Other description");
        assertEquals(lessonForUpdate, lessonDAO.update(lessonForUpdate));
    }


    @Test
    @ShouldMatchDataSet(value = {DS_LESSON_AFTER_REMOVE})
    public void testRemoveLesson() {
        lessonDAO.remove(predefinedLessonInitialization());
    }

    @Test
    @ShouldMatchDataSet(value = {DS_LESSON_AFTER_REMOVE})
    public void testRemoveByIdLesson() {
        lessonDAO.removeById(predefinedLessonInitialization().getId());
    }

    @Test
    @ShouldMatchDataSet(value = {DS_LESSON_AFTER_REMOVE})
    public void testRemoveReturnLessonEntity() {
        LessonEntity lessonForRemove = predefinedLessonInitialization();
        assertEquals(lessonForRemove, lessonDAO.remove(lessonForRemove));
    }

    @Test
    public void testGetReturnLessonEntity() {
        LessonEntity lessonForGet = predefinedLessonInitialization();
        assertNotNull(lessonDAO.getById(lessonForGet.getId()));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON}, excludeColumns = {"id"})
    public void testGetAllLesson() {
        List<LessonEntity> lessonEntities = lessonDAO.getAllLessons();
        LessonEntity predefinedLesson = predefinedLessonInitialization();
        predefinedLesson = lessonDAO.getById(predefinedLesson.getId());
        assertThat(lessonEntities, hasItem(predefinedLesson));
        assertThat(lessonEntities, is(notNullValue()));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON})
    public void testSaveNullLessonTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> lessonDAO.save(null))
                .isInstanceOf(EJBException.class);

    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON})
    public void testRemoveNullLessonTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> lessonDAO.remove(null))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON})
    public void testUpdateNullLessonTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> lessonDAO.update(null))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON})
    public void testSaveNotValidLessonTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> lessonDAO.save(new LessonEntity()))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON})
    public void testUpdateNotValidLessonTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> lessonDAO.update(new LessonEntity()))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON})
    public void testRemoveNotValidLessonTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> lessonDAO.remove(new LessonEntity()))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON})
    public void testSaveLessonThatExistThrowsEntityIsAlreadyExistException() throws EntityIsAlreadyExistException {
        LessonEntity lessonThatExists = predefinedLessonInitialization();
        assertThatThrownBy(() -> lessonDAO.save(lessonThatExists))
                .isInstanceOf(EntityIsAlreadyExistException.class).hasMessage("Field with id = "
                + lessonThatExists.getId() + " already exists in database");
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON})
    public void testUpdateLessonThatNotExistThrowsEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        LessonEntity lessonThatNotExists = predefinedLessonInitialization();
        lessonThatNotExists.setId(notExistingId);
        assertThatThrownBy(() -> lessonDAO.update(lessonThatNotExists))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_LESSON})
    public void testRemoveLessonThatNotExistThrowsEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        LessonEntity lessonThatNotExists = lessonInitializationForTests();
        lessonThatNotExists.setId(notExistingId);
        assertThatThrownBy(() -> lessonDAO.remove(lessonThatNotExists))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }

    private LessonEntity predefinedLessonInitialization() {
        LessonEntity lessonEntity = new LessonEntity();
        CourseEntity predefinedCourse = courseDAO.getById(1L);
        lessonEntity.setCourse(predefinedCourse);
        lessonEntity.setCreateDate(Date.valueOf("2016-08-11"));
        lessonEntity.setTopic("topic");
        lessonEntity.setDescription("Some description");
        lessonEntity.setType(1L);
        lessonEntity.setOrderNum(764L);
        lessonEntity.setId(1L);
        return lessonEntity;
    }


    public LessonEntity lessonInitializationForTests() {
        CourseEntity predefinedCourse = courseDAO.getById(1L);
        LessonEntity lessonEntity = new LessonEntity();
        lessonEntity.setCourse(predefinedCourse);
        lessonEntity.setCreateDate(Date.valueOf("2016-08-11"));
        lessonEntity.setTopic("topic");
        lessonEntity.setDescription("Some description");
        lessonEntity.setType(1L);
        lessonEntity.setOrderNum(764L);
        return lessonEntity;
    }

}
