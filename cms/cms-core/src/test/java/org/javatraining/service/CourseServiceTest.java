package org.javatraining.service;

import org.javatraining.dao.CourseDAO;
import org.javatraining.dao.NewsDAO;
import org.javatraining.dao.exception.EntityNotExistException;
import org.javatraining.entity.enums.PersonRole;
import org.javatraining.entity.util.Pair;
import org.javatraining.model.CourseVO;
import org.javatraining.model.NewsVO;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.*;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import java.sql.Date;
import java.sql.Timestamp;

import static org.assertj.core.api.StrictAssertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by vika on 14.06.15.
 */

@RunWith(Arquillian.class)
@Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
@UsingDataSet(value = "datasets/course-service-test/course/one-course.json")
public class CourseServiceTest {

    private static final String DS_DIR = "course-service-test/";
    private static final String DS_EMPTY = DS_DIR + "empty.json";
    private static final String DS_COURSE = DS_DIR + "course/one-course.json";
    private static final String DS_COURSE_AFTER_REMOVE = DS_DIR + "course/course-after-remove.json";
    private static final String DS_COURSE_AFTER_SAVE = DS_DIR + "course/course-expected-after-save.json";
    private static final String DS_NEWS_AFTER_UPDATE = DS_DIR + "course/news-after-update.json";

    @EJB
    private CourseService courseService;

    @EJB
    private CourseDAO courseDAO;

    @EJB
    private NewsDAO newsDAO;


    @EJB
    private PersonService personService;

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class)
                .addPackage("org.javatraining.service.impl")
                .addPackage("org.javatraining.service")
                .addPackage("org.javatraining.model.conversion")
                .addPackage("org.javatraining.service.exception")
                .addPackage("org.javatraining.dao.exception")
                .addPackage("org.javatraining.model")
                .addPackage("org.javatraining.entity")
                .addPackage("org.javatraining.entity.enums")
                .addPackage("org.javatraining.dao")
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
    public void testSaveReturnCourseVO() {
        CourseVO courseVO = courseVOInitialization();
        assertEquals(courseVO, courseService.saveCourse(courseVO));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE_AFTER_SAVE}, excludeColumns = {"id"})
    public void testSaveCourseVO() {
        CourseVO courseVO = courseVOInitialization();
        courseService.saveCourse(courseVO);
    }


    @Test
    public void testUpdateReturnCourseVO() {
        CourseVO courseVO = courseVOInitialization();
        courseVO.setName("Other name");
        assertEquals(courseVO, courseService.updateCourse(courseVO));
    }


    @Test
    public void testRemoveReturnCourseVO() {
        CourseVO courseVO = predefinedCourseVO();
        assertEquals(courseVO, courseService.removeCourse(courseVO));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE_AFTER_REMOVE})
    public void testRemoveCourse() {
        CourseVO courseVO = predefinedCourseVO();
        courseService.removeCourse(courseVO);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE_AFTER_REMOVE})
    public void testRemoveTwoCourse() {
        CourseVO courseVO = predefinedCourseVO();
        courseService.removeCourse(courseVO);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_NEWS_AFTER_UPDATE}, excludeColumns = {"id"})
    public void testUpdateNews() {
        NewsVO newsVO = predefinedNewsVOInitialization();
        courseService.updateNews(newsVO);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testGetNewsById() {
        NewsVO newsVO = predefinedNewsVOInitialization();
        assertEquals(newsVO, courseService.getNewsById(newsVO.getId()));
    }


    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE}, excludeColumns = {"id"})
    public void testGetReturnCourseVO() {
        CourseVO predefinedCourseVO = predefinedCourseVO();
        CourseVO courseVO = courseService.getCourseById(predefinedCourseVO.getId());
        assertNotNull(courseVO);
    }

    @Test
    public void testGetAllCourses() {
        assertNotNull(courseService.getAll());
    }

    @Test
    public void testGetAllPersonsFromCourseByRole() {
        CourseVO courseVO = predefinedCourseVO();
        assertNotNull(courseService.getAllPersonsFromCourseByRole(courseVO, PersonRole.TEACHER));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testUpdateNotValidCourseTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> courseService.updateCourse(new CourseVO()))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testRemoveNotValidCourseTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> courseService.removeCourse(new CourseVO()))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testSaveNotValidCourseTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> courseService.saveCourse(new CourseVO()))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testSaveNullCourseTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> courseService.saveCourse(null))
                .isInstanceOf(EJBException.class);

    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testRemoveNullCourseTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> courseService.removeCourse(null))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testUpdateNullCourseTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> courseService.updateCourse(null))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testGetAllNewsFromCourse() {
        CourseVO courseVO = predefinedCourseVO();
        assertNotNull(courseService.getAllNewsFromCourse(courseVO));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testGetAllNews() {
        assertNotNull(courseService.getAllNews());

    }


    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testGetCourseByIdForNotExistingIdShouldReturnEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        assertThatThrownBy(() -> courseService.getCourseById(notExistingId))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with " + notExistingId + " does not exist in database");

    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testGetNewsByIdForNotExistingIdShouldReturnEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        assertThatThrownBy(() -> courseService.getNewsById(notExistingId))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }


    @Test
    public void testAddNewsToCourse() {
        CourseVO courseVO = predefinedCourseVO();
        NewsVO newsVOForSave = newsVOInitializationForTests();
        assertEquals(newsVOForSave, courseService.addNewsToCourse(courseVO, newsVOForSave));
    }

    private CourseVO courseVOInitialization() {
        CourseVO courseVO = new CourseVO();
        courseVO.setName("JavaEE");
        courseVO.setStartDate(Date.valueOf("2015-10-10"));
        courseVO.setEndDate(Date.valueOf("2016-11-11"));
        courseVO.setDescription("Java");
        return courseVO;
    }

    private CourseVO predefinedCourseVO() {
        CourseVO courseVO = new CourseVO();
        courseVO.setId(1L);
        courseVO.setName("courseName");
        courseVO.setStartDate(Date.valueOf("2014-01-10"));
        courseVO.setEndDate(Date.valueOf("2015-07-31"));
        courseVO.setDescription("courseDescription");

        return courseVO;
    }



    private NewsVO predefinedNewsVOInitialization() {
        Long predefinedNewsId = 1L;
        NewsVO newsVO = new NewsVO();
        newsVO.setId(predefinedNewsId);
        newsVO.setContent("newsDescription");
        newsVO.setTitle("title");
        newsVO.setDate(Timestamp.valueOf("2015-10-02 18:48:05"));
        return newsVO;
    }

    private NewsVO newsVOInitializationForTests() {
        NewsVO newsVO = new NewsVO();
        newsVO.setContent("newsDescription");
        newsVO.setTitle("title");
        newsVO.setDate(Timestamp.valueOf("2015-10-02 18:48:05"));
        return newsVO;
    }

}
