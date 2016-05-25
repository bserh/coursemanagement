package org.javatraining.dao;

import org.javatraining.dao.exception.EntityIsAlreadyExistException;
import org.javatraining.dao.exception.EntityNotExistException;
import org.javatraining.entity.CourseEntity;
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

import static org.assertj.core.api.StrictAssertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(Arquillian.class)
@Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
@UsingDataSet(value = "dao-tests/course/course.json")
public class CourseDAOTest {

    @EJB
    CourseDAO courseDAO;
    private static final String DS_DIR = "dao-tests/";
    private static final String DS_EMPTY = DS_DIR + "empty.json";

    private static final String DS_COURSE = DS_DIR + "course/course.json";
    private static final String DS_COURSE_AFTER_UPDATE = DS_DIR + "course/expected-after-update.json";
    private static final String DS_COURSE_AFTER_SAVE = DS_DIR + "course/expected-after-save.json";

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
                .addPackage("org.assertj.core.groups")
                .addPackage("org.assertj.core.presentation")
                .addPackage("org.assertj.core.internal")
                .addClass(Pair.class)
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        return war;
    }

    @Test
    public void testCourseDAOShouldBeInjected() throws Exception {
        assertNotNull(courseDAO);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testGetByIdForNotExistingIdShouldReturnEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        assertThatThrownBy(() -> courseDAO.getById(notExistingId))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with " + notExistingId + " does not exist in database");

    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testRemoveByIdForNotExistingIdShouldReturnEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        assertThatThrownBy(() -> courseDAO.removeById(notExistingId))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }


    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testSaveCourseThatExistThrowsEntityIsAlreadyExistException() throws EntityIsAlreadyExistException {
        CourseEntity courseThatExists = predefinedCourseInitialization();
        assertThatThrownBy(() -> courseDAO.save(courseThatExists))
                .isInstanceOf(EntityIsAlreadyExistException.class).hasMessage("Field with id = "
                + courseThatExists.getId() + " already exists in database");
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testUpdateCourseThatNotExistThrowsEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        CourseEntity courseThatNotExists = courseInitializationForTests();
        courseThatNotExists.setId(notExistingId);
        assertThatThrownBy(() -> courseDAO.update(courseThatNotExists))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testRemoveCourseThatNotExistThrowsEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        CourseEntity courseThatNotExists = courseInitializationForTests();
        courseThatNotExists.setId(notExistingId);
        assertThatThrownBy(() -> courseDAO.remove(courseThatNotExists))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }

    @Test
    public void testSaveCourseReturnCourseEntity() {
        CourseEntity predefinedCourse = courseInitializationForTests();
        assertEquals(predefinedCourse, courseDAO.save(predefinedCourse));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE_AFTER_SAVE})
    public void testSaveCourse() {
        CourseEntity courseForSave = courseInitializationForTests();
        courseDAO.save(courseForSave);
    }

    @Test
    public void testUpdateCourseReturnNewsEntity() {
        CourseEntity courseEntity = courseInitializationForTests();
        assertEquals(courseEntity, courseDAO.update(courseEntity));
    }

    @Test
    public void testRemoveReturnCourseEntity() {
        CourseEntity courseEntity = predefinedCourseInitialization();
        assertEquals(courseEntity, courseDAO.remove(courseEntity));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE_AFTER_UPDATE}, excludeColumns = {"id"})
    public void testUpdateCourse() {
        CourseEntity courseForUpdate = predefinedCourseInitialization();
        courseForUpdate.setName("OtherName");
        courseDAO.update(courseForUpdate);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY})
    public void testRemoveCourse() {
        courseDAO.remove(predefinedCourseInitialization());
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY})
    public void testRemoveByIdCourse() {
        courseDAO.removeById(predefinedCourseInitialization().getId());
    }


    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testSaveNullCourseTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> courseDAO.save(null))
                .isInstanceOf(EJBException.class);

    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testRemoveNullCourseTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> courseDAO.remove(null))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testUpdateNullCourseTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> courseDAO.update(null))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testSaveNotValidCourseTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> courseDAO.save(new CourseEntity()))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testUpdateNotValidCourseTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> courseDAO.update(new CourseEntity()))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testRemoveNotValidCourseTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> courseDAO.remove(new CourseEntity()))
                .isInstanceOf(EJBException.class);
    }


    @Test
    public void testGetReturnCourseEntity() {
        CourseEntity courseEntity = courseInitializationForTests();
        courseDAO.save(courseEntity);
        assertEquals(courseEntity, courseDAO.getById(courseEntity.getId()));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE}, excludeColumns = {"id"})
    public void testGetAllCourses() {
        assertNotNull(courseDAO.getAllCourses());
    }

    @Test
    @ShouldMatchDataSet(value = DS_EMPTY)
    public void testClearDataBaseShouldBeEmpty() {
        courseDAO.clear();

    }


    private CourseEntity courseInitializationForTests() {
        CourseEntity courseEntity = new CourseEntity();
        courseEntity.setName("courseName");
        courseEntity.setStartDate(Date.valueOf("2015-10-10"));
        courseEntity.setEndDate(Date.valueOf("2016-11-11"));
        courseEntity.setDescription("Java");
        return courseEntity;
    }

    private CourseEntity predefinedCourseInitialization() {
        CourseEntity predefinedCourse = new CourseEntity();
        predefinedCourse.setId((long) 1);
        predefinedCourse.setName("courseName");
        predefinedCourse.setStartDate(Date.valueOf("2014-01-10"));
        predefinedCourse.setEndDate(Date.valueOf("2015-07-31"));
        predefinedCourse.setDescription("courseDescription");
        return predefinedCourse;
    }


}