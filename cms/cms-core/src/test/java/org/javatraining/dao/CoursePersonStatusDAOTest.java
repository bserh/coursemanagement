package org.javatraining.dao;

import org.javatraining.dao.exception.EntityIsAlreadyExistException;
import org.javatraining.dao.exception.EntityNotExistException;
import org.javatraining.entity.CoursePersonStatusEntity;
import org.javatraining.entity.enums.CourseStatus;
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

import static org.assertj.core.api.StrictAssertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by vika on 27.06.15.
 */

@RunWith(Arquillian.class)
@Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
@UsingDataSet(value = "dao-tests/course-person-status/person-course.json")
public class CoursePersonStatusDAOTest {
    @EJB
    CoursePersonStatusDAO coursePersonStatusDAO;

    @EJB
    CourseDAO courseDAO;

    @EJB
    PersonDAO personDAO;


    private static final String DS_DIR = "dao-tests/";
    private static final String DS_EMPTY = DS_DIR + "empty.json";

    private static final String DS_COURSE_PERSON_STATUS = DS_DIR + "course-person-status/person-course.json";

    private static final String DS_COURSE_PERSON_STATUS_AFTER_SAVE = DS_DIR + "course-person-status/expected-after-save.json";
    private static final String DS_COURSE_PERSON_STATUS_AFTER_UPDATE = DS_DIR + "course-person-status/expected-after-update.json";
    private static final String DS_COURSE_PERSON_STATUS_AFTER_REMOVE = DS_DIR + "course-person-status/expected-after-remove.json";

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
    public void testCoursePersonStatusDAOShouldBeInjected() throws Exception {
        assertNotNull(coursePersonStatusDAO);
    }

    @Test
    public void testRemoveReturnCoursePersonStatusEntity() {
        CoursePersonStatusEntity predefinedCoursePersonStatus = predefinedCoursePersonStatusInitialization();
        assertEquals(predefinedCoursePersonStatus, coursePersonStatusDAO.remove(predefinedCoursePersonStatus));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE_PERSON_STATUS_AFTER_UPDATE}, excludeColumns = {"id"})
    public void testUpdateCoursePersonStatus() {
        CoursePersonStatusEntity predefinedCoursePersonStatus = predefinedCoursePersonStatusInitialization();
        predefinedCoursePersonStatus.setCourseStatus(CourseStatus.REQUESTED);
        coursePersonStatusDAO.update(predefinedCoursePersonStatus);
    }

    @Test
    public void testUpdateReturnCoursePersonStatusEntity() {
        CoursePersonStatusEntity predefinedCoursePersonStatus = predefinedCoursePersonStatusInitialization();
        predefinedCoursePersonStatus.setCourseStatus(CourseStatus.REQUESTED);
        assertEquals(predefinedCoursePersonStatus, coursePersonStatusDAO.update(predefinedCoursePersonStatus));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE_PERSON_STATUS_AFTER_SAVE}, excludeColumns = {"id"})
    public void testSaveCoursePersonStatus() {
        CoursePersonStatusEntity coursePersonStatusForSave = coursePersonStatusInitializationForTests();
        coursePersonStatusDAO.save(coursePersonStatusForSave);
    }

    @Test
    public void testSaveReturnCoursePersonStatusEntity() {
        CoursePersonStatusEntity coursePersonStatusForSave = coursePersonStatusInitializationForTests();
        assertEquals(coursePersonStatusForSave, coursePersonStatusDAO.save(coursePersonStatusForSave));
    }

    @Test
    public void testGetReturnCoursePersonEntity() {
        CoursePersonStatusEntity coursePersonStatusForGet = predefinedCoursePersonStatusInitialization();
        assertEquals(coursePersonStatusForGet, coursePersonStatusDAO.getById(coursePersonStatusForGet.getId()));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE_PERSON_STATUS_AFTER_REMOVE})
    public void testRemoveCoursePersonStatus() {
        coursePersonStatusDAO.remove(predefinedCoursePersonStatusInitialization());
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE_PERSON_STATUS})
    public void testGetByIdForNotExistingIdShouldReturnEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        assertThatThrownBy(() -> coursePersonStatusDAO.getById(notExistingId))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with " + notExistingId + " does not exist in database");

    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE_PERSON_STATUS})
    public void testRemoveByIdForNotExistingIdShouldReturnEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        assertThatThrownBy(() -> coursePersonStatusDAO.removeById(notExistingId))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with " + notExistingId + " does not exist in database");

    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE_PERSON_STATUS})
    public void testSaveCoursePersonStatusThatExistThrowsEntityIsAlreadyExistException() throws EntityIsAlreadyExistException {
        CoursePersonStatusEntity coursePersonStatusThatExists = predefinedCoursePersonStatusInitialization();
        assertThatThrownBy(() -> coursePersonStatusDAO.save(coursePersonStatusThatExists))
                .isInstanceOf(EntityIsAlreadyExistException.class).hasMessage(String.format("Status for %s and %s is already set",
                coursePersonStatusThatExists.getPerson(), coursePersonStatusThatExists.getCourse()));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE_PERSON_STATUS})
    public void testUpdateCoursePersonStatusThatNotExistThrowsEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        CoursePersonStatusEntity coursePersonStatusThatNotExists = predefinedCoursePersonStatusInitialization();
        coursePersonStatusThatNotExists.setId(notExistingId);
        assertThatThrownBy(() -> coursePersonStatusDAO.update(coursePersonStatusThatNotExists))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE_PERSON_STATUS})
    public void testRemoveCoursePersonStatusThatNotExistThrowsEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        CoursePersonStatusEntity coursePersonStatusThatNotExists = predefinedCoursePersonStatusInitialization();
        coursePersonStatusThatNotExists.setId(notExistingId);
        assertThatThrownBy(() -> coursePersonStatusDAO.remove(coursePersonStatusThatNotExists))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }

    private CoursePersonStatusEntity predefinedCoursePersonStatusInitialization() {
        Long predefinedCoursePersonId = 1L;
        CoursePersonStatusEntity coursePersonStatusEntity = new CoursePersonStatusEntity(CourseStatus.SIGNED);
        coursePersonStatusEntity.setCourse(courseDAO.getById(1L));
        coursePersonStatusEntity.setPerson(personDAO.getById(1L));
        coursePersonStatusEntity.setId(predefinedCoursePersonId);

        return coursePersonStatusEntity;
    }

    private CoursePersonStatusEntity coursePersonStatusInitializationForTests() {
        CoursePersonStatusEntity coursePersonStatusEntity = new CoursePersonStatusEntity(CourseStatus.UNSIGNED);
        coursePersonStatusEntity.setCourse(courseDAO.getById(1L));
        coursePersonStatusEntity.setPerson(personDAO.getById(2L));

        return coursePersonStatusEntity;
    }

}

