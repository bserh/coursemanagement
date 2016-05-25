package org.javatraining.dao;

import org.javatraining.dao.exception.EntityIsAlreadyExistException;
import org.javatraining.dao.exception.EntityNotExistException;
import org.javatraining.entity.CourseEntity;
import org.javatraining.entity.NewsEntity;
import org.javatraining.entity.util.Pair;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.*;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import java.sql.Date;
import java.sql.Timestamp;

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
@UsingDataSet(value = "dao-tests/news/news.json")
public class NewsDAOTest {

    @EJB
    NewsDAO newsDAO;

    @EJB
    CourseDAO courseDAO;

    private static final String DS_DIR = "dao-tests/";
    private static final String DS_EMPTY = DS_DIR + "empty.json";

    private static final String DS_NEWS = DS_DIR + "news/news.json";
    private static final String DS_NEWS_AFTER_UPDATE = DS_DIR + "news/expected-after-update.json";
    private static final String DS_NEWS_AFTER_SAVE = DS_DIR + "news/expected-after-save.json";
    private static final String DS_NEWS_AFTER_REMOVE = DS_DIR + "news/expected-after-remove.json";


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
    public void testPersonDAOShouldBeInjected() throws Exception {
        assertThat(newsDAO, is(notNullValue()));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_NEWS_AFTER_SAVE}, excludeColumns = {"id"})
    public void testSaveNews() {
        newsDAO.save(newsEntityInitializationForTests());
    }

    @Test
    public void testSaveReturnNewsEntity() {
        NewsEntity newsForSave = newsEntityInitializationForTests();
        assertEquals(newsForSave, newsDAO.save(newsForSave));
    }

    @Test
    @ShouldMatchDataSet(value = DS_NEWS_AFTER_REMOVE)
    public void testRemoveByIdNews() {
        newsDAO.removeById(predefinedNewsInitialization().getId());
    }

    @Test
    @ShouldMatchDataSet(value = DS_NEWS_AFTER_REMOVE)
    public void testRemoveNews() {
        newsDAO.remove(predefinedNewsInitialization());
    }

    @Test
    public void testRemoveReturnNewsEntity() {
        NewsEntity newsForRemove = predefinedNewsInitialization();
        assertEquals(newsForRemove, newsDAO.remove(newsForRemove));
    }

    @Test
    public void testUpdateReturnNewsEntity() {
        NewsEntity newsForUpdate = predefinedNewsInitialization();
        newsForUpdate.setTitle("otherTitle");
        assertEquals(newsForUpdate, newsDAO.update(newsForUpdate));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_NEWS_AFTER_UPDATE}, excludeColumns = {"id"})
    public void testUpdateNews() {
        NewsEntity newsForUpdate = predefinedNewsInitialization();
        newsForUpdate.setTitle("otherTitle");
        newsDAO.update(newsForUpdate);
    }

    @Test
    public void testGetReturnNewsEntity() {
        NewsEntity newsForGet = predefinedNewsInitialization();
        assertThat(newsForGet, is(equalTo(newsDAO.getById(newsForGet.getId()))));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_NEWS})
    public void testGetNews() {
        assertNotNull(newsDAO.getById(predefinedNewsInitialization().getId()));
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_NEWS})
    public void testSaveNullNewsTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> newsDAO.save(null))
                .isInstanceOf(EJBException.class);

    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_NEWS})
    public void testRemoveNullNewsTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> newsDAO.remove(null))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_NEWS})
    public void testUpdateNullNewsTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> newsDAO.update(null))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_NEWS})
    public void testSaveNotValidNewsTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> newsDAO.save(new NewsEntity()))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_NEWS})
    public void testUpdateNotValidNewsTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> newsDAO.update(new NewsEntity()))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_NEWS})
    public void testRemoveNotValidNewsTrowEJBException() throws EJBException {
        assertThatThrownBy(() -> newsDAO.remove(new NewsEntity()))
                .isInstanceOf(EJBException.class);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_NEWS})
    public void testGetByIdForNotExistingIdShouldReturnEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        assertThatThrownBy(() -> newsDAO.getById(notExistingId))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with " + notExistingId + " does not exist in database");

    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_NEWS})
    public void testRemoveByIdForNotExistingIdShouldReturnEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        assertThatThrownBy(() -> newsDAO.removeById(notExistingId))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }


    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_NEWS})
    public void testSaveNewsThatExistThrowsEntityIsAlreadyExistException() throws EntityIsAlreadyExistException {
        NewsEntity newsThatExists = predefinedNewsInitialization();
        assertThatThrownBy(() -> newsDAO.save(newsThatExists))
                .isInstanceOf(EntityIsAlreadyExistException.class).hasMessage("Field with id = "
                + newsThatExists.getId() + " already exists in database");
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_NEWS})
    public void testUpdateNewsThatNotExistThrowsEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        NewsEntity newsThatExists = predefinedNewsInitialization();
        newsThatExists.setId(notExistingId);
        assertThatThrownBy(() -> newsDAO.update(newsThatExists))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_NEWS})
    public void testRemoveNewsThatNotExistThrowsEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        NewsEntity newsThatExists = predefinedNewsInitialization();
        newsThatExists.setId(notExistingId);
        assertThatThrownBy(() -> newsDAO.remove(newsThatExists))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }

    private NewsEntity newsEntityInitializationForTests() {
        NewsEntity newsEntity = new NewsEntity();
        Long predefinedCourseId = (long) 1;
        CourseEntity courseEntity = courseDAO.getById(predefinedCourseId);
        newsEntity.setCourse(courseEntity);
        newsEntity.setDescription("newsDescription");
        newsEntity.setTitle("Sometitle");
        newsEntity.setDate(Timestamp.valueOf("2015-10-02 18:48:05"));
        return newsEntity;
    }

    public CourseEntity predefinedCourse() {
        Long predefinedCourseId = 1L;
        CourseEntity predefinedCourse = new CourseEntity("courseName", "courseDescription", Date.valueOf("2014-01-10"), Date.valueOf("2015-07-31"));
        predefinedCourse.setId(predefinedCourseId);
        return predefinedCourse;
    }

    private NewsEntity predefinedNewsInitialization() {
        Long predefinedNewsId = 1L;
        CourseEntity courseEntity = predefinedCourse();
        NewsEntity newsEntity = new NewsEntity();
        newsEntity.setId(predefinedNewsId);
        newsEntity.setCourse(courseEntity);
        newsEntity.setDescription("newsDescription");
        newsEntity.setTitle("title");
        newsEntity.setDate(Timestamp.valueOf("2015-10-02 18:48:05"));
        return newsEntity;
    }

}
