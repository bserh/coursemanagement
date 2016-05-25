package org.javatraining.service;

import org.hamcrest.core.IsInstanceOf;
import org.javatraining.dao.*;
import org.javatraining.dao.exception.EntityIsAlreadyExistException;
import org.javatraining.dao.exception.EntityNotExistException;
import org.javatraining.entity.PersonEntity;
import org.javatraining.entity.enums.PersonRole;
import org.javatraining.entity.util.Pair;
import org.javatraining.model.CourseVO;
import org.javatraining.model.MarkVO;
import org.javatraining.model.PersonVO;
import org.javatraining.model.PracticeLessonVO;
import org.javatraining.model.conversion.PersonConverter;
import org.javatraining.service.exception.UnsupportedOperationException;
import org.javatraining.service.impl.PersonServiceImpl;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.*;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.Date;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.fail;

/**
 * Created by olga on 15.06.15.
 */

@RunWith(Arquillian.class)
@UsingDataSet(value = "datasets/person-service-test/person/one-person.json")
@Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
public class PersonServiceTest {

    private static final String DS_DIR = "datasets/person-service-test";
    private static final String DS_EMPTY = DS_DIR + "/empty.json";

    private static final String DS_PERSON = DS_DIR + "/person/one-person.json";
    private static final String DS_PERSON_AFTER_UPDATE = DS_DIR + "/person/expected-after-update.json";
    private static final String DS_PERSON_AFTER_SAVE = DS_DIR + "/person/expected-after-save.json";

    private static final String DS_COURSE = DS_DIR + "/course/one-course.json";
    private static final String DS_COURSE_PERSON = DS_DIR + "/course/person-course.json";
    private static final String DS_PRACTICE = DS_DIR + "/practice-lesson/one-lesson-and-practice.json";
    private static final String DS_MARK = DS_DIR + "/practice-lesson/mark.json";

    private static final PersonVO predefinedPerson;
    private static final CourseVO predefinedCourse;
    private static final PracticeLessonVO predefinedPractice;
    private static final MarkVO predefinedMark;
    private static final int predefinedPersonCoursesCnt;
    private static final String predefinedPersonNameForUpdate;
    private static final int predefinedMarkToSave;
    private static final int predefinedMarksCnt;
    private static final PersonVO personForSaving;
    private static final String notExistingEmail;
    private static final Long notExistingId;


    static {
        predefinedPerson = new PersonVO(1L, "teacherName", "teacherLastName", "teacher@gmail.com", PersonRole.TEACHER);
        predefinedPerson.setSecondName("teacherSecondName");
        personForSaving = new PersonVO(null, "testedName", "testedLastName", "tested@gmail.com", PersonRole.STUDENT);
        personForSaving.setSecondName("testedSecondName");
        predefinedPersonNameForUpdate = "updatedName";

        predefinedCourse = new CourseVO(1L, "courseName", "courseDescription");
        predefinedCourse.setStartDate(Date.valueOf("2014-01-10"));
        predefinedCourse.setEndDate(Date.valueOf("2015-07-31"));
        predefinedPersonCoursesCnt = 1;

        predefinedPractice = new PracticeLessonVO(1L, "practiceTask");
        predefinedMarkToSave = 100;
        predefinedMarksCnt = 1;
        predefinedMark = new MarkVO(1L, predefinedMarkToSave);

        notExistingEmail = "12345";
        notExistingId = Long.MIN_VALUE;
    }


    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackage(PersonEntity.class.getPackage())
                .addPackage(PersonVO.class.getPackage())
                .addPackage(PersonConverter.class.getPackage())
                .addPackage(EntityNotExistException.class.getPackage())
                .addPackage(PersonRole.class.getPackage())
                .addClasses(PersonDAO.class, CourseDAO.class, MarkDAO.class,
                        PracticeLessonDAO.class, CoursePersonStatusDAO.class, GenericDAO.class)
                .addClass(Pair.class)
                .addClasses(PersonService.class, PersonServiceImpl.class, UnsupportedOperationException.class)
                .addAsResource(DS_DIR)
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml");
    }

    @Rule
    public ExpectedException thrownException = ExpectedException.none();

    @EJB
    PersonService personService;

    @Test
    public void testPersonServiceShouldBeInjected() throws Exception {
        assertThat(personService, is(notNullValue()));
    }

    @Test
    public void testGetById() throws Exception {
        PersonVO predefinedPersonFromService = personService.getById(predefinedPerson.getId());
        assertThat(predefinedPersonFromService, is(equalTo(predefinedPerson)));
    }

    @Test
    public void testGetByIdForNotExistingIdShouldReturnNull() throws Exception {
        PersonVO personWithNotExistingId = personService.getById(notExistingId);
        assertThat(personWithNotExistingId, is(nullValue()));
    }

    @Test
    public void testGetByIdForNullIdShouldThrowConstraintViolationException() throws Exception {
        thrownException.expect(javax.ejb.EJBException.class);
        thrownException.expectCause(IsInstanceOf.<Throwable>instanceOf(ConstraintViolationException.class));
        personService.getById(null);
    }

    @Test
    public void testGetByEmail() throws Exception {
        PersonVO predefinedPersonFromServiceByEmail = personService.getByEmail(predefinedPerson.getEmail());
        assertThat(predefinedPersonFromServiceByEmail, is(equalTo(predefinedPerson)));
    }

    @Test
    public void testGetByEmailForNotExistingEmailShouldReturnNull() throws Exception {
        PersonVO personWithNotExistingEmail = personService.getByEmail(notExistingEmail);
        assertThat(personWithNotExistingEmail, is(nullValue()));
    }

    @Test
    public void testGetByEmailForNullEmailShouldThrowConstraintViolationException() throws Exception {
        try {
            personService.getByEmail(null);
            fail("Must throw exception");
        } catch (EJBException e) {
            assertThat(e.getCause(), is(instanceOf(ConstraintViolationException.class)));
            if (!checkNotNullArgumentViolationException((ConstraintViolationException) e.getCause())) {
                fail("Unexpected exception");
            }
        }
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON_AFTER_SAVE}, excludeColumns = {"id", "phone"})
    public void testSave() throws Exception {
        personService.save(personForSaving);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON}, excludeColumns = {"id", "phone"})
    public void testSaveForAlreadyExistingPersonShouldDoNothing() throws Exception {
        personService.save(predefinedPerson);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON}, excludeColumns = {"id", "phone"})
    public void testSaveForNullPersonShouldThrowConstraintViolationException() throws Exception {
        try {
            personService.save(null);
            fail("Must throw exception");
        } catch (EJBException e) {
            assertThat(e.getCause(), is(instanceOf(ConstraintViolationException.class)));
            if (!checkNotNullArgumentViolationException((ConstraintViolationException) e.getCause())) {
                fail("Unexpected exception");
            }
        }
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON}, excludeColumns = {"id", "phone"})
    public void testSaveForNotValidPersonShouldThrowConstraintViolationException() throws Exception {
        try {
            personService.save(new PersonVO());
            fail("Must throw exception");
        } catch (EJBException e) {
            assertThat(e.getCause(), is(instanceOf(ConstraintViolationException.class)));
            if (!checkValidPersonViolationException((ConstraintViolationException) e.getCause())) {
                fail("Unexpected exception");
            }
        }
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON_AFTER_UPDATE}, excludeColumns = {"id", "phone"})
    public void testUpdate() throws Exception {
        predefinedPerson.setName(predefinedPersonNameForUpdate);
        personService.update(predefinedPerson);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON}, excludeColumns = {"id", "phone"})
    public void testUpdateForNullPersonShouldThrowConstraintViolationException() throws Exception {
        try {
            personService.update(null);
            fail("Must throw exception");
        } catch (EJBException e) {
            assertThat(e.getCause(), is(instanceOf(ConstraintViolationException.class)));
            if (!checkNotNullArgumentViolationException((ConstraintViolationException) e.getCause())) {
                fail("Unexpected exception");
            }
        }
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON}, excludeColumns = {"id", "phone"})
    public void testUpdateForNotValidPersonShouldThrowConstraintViolationException() throws Exception {
        try {
            personService.update(new PersonVO());
            fail("Must throw exception");
        } catch (EJBException e) {
            assertThat(e.getCause(), is(instanceOf(ConstraintViolationException.class)));
            if (!checkValidPersonViolationException((ConstraintViolationException) e.getCause())) {
                fail("Unexpected exception");
            }
        }
    }

    @Test
    @ShouldMatchDataSet(value = DS_EMPTY)
    public void testRemove() throws Exception {
        personService.remove(predefinedPerson);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON}, excludeColumns = {"id", "phone"})
    public void testRemoveForNullPersonShouldThrowConstraintViolationException() throws Exception {
        try {
            personService.remove(null);
            fail("Must throw exception");
        } catch (EJBException e) {
            assertThat(e.getCause(), is(instanceOf(ConstraintViolationException.class)));
            if (!checkNotNullArgumentViolationException((ConstraintViolationException) e.getCause())) {
                fail("Unexpected exception");
            }
        }
    }

    @Test
    @ShouldMatchDataSet(value = DS_EMPTY, excludeColumns = {"id", "phone"})
    public void testRemoveForNotValidPersonShouldRemoveCorrectly() throws Exception {
        PersonVO notValidPerson = new PersonVO();
        notValidPerson.setId(predefinedPerson.getId());
        personService.remove(notValidPerson);
    }

    @Test
    @UsingDataSet(value = {DS_PERSON, DS_COURSE, DS_COURSE_PERSON})
    public void testGetCoursesForNotExistingPerson() throws Exception {
        PersonVO notExistingPerson = createNotExistingPerson();
        try {
            personService.getPersonCoursesWithStatuses(notExistingPerson);
            fail("Must throw exception");
        } catch (EJBException e) {
            assertThat(e.getCause(), is(instanceOf(ConstraintViolationException.class)));
            if (!checkNotNullArgumentViolationException((ConstraintViolationException) e.getCause())) {
                fail("Unexpected exception");
            }
        }
    }

    @Test
    @UsingDataSet(value = {DS_PERSON, DS_COURSE})
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON, DS_COURSE, DS_COURSE_PERSON})
    public void testAddPersonToCourse() throws Exception {
        personService.addPersonRequestForCourse(predefinedPerson, predefinedCourse);
    }

    @Test(expected = EntityIsAlreadyExistException.class)
    @UsingDataSet(value = {DS_PERSON, DS_COURSE, DS_COURSE_PERSON})
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON, DS_COURSE, DS_COURSE_PERSON})
    public void testAddPersonToCourseForPersonAlreadyAddedToCourse() throws Exception {
        personService.addPersonRequestForCourse(predefinedPerson, predefinedCourse);
    }

    @Test
    @UsingDataSet(value = {DS_PERSON})
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON})
    public void testAddPersonToCourseForNotExistingCourseShouldThrowException() throws Exception {
        try {
            CourseVO notExistingCourse = createNotExistingCourse();
            personService.addPersonRequestForCourse(predefinedPerson, notExistingCourse);
            fail("Must throw exception");
        } catch (EJBException e) {
            assertThat(e.getCause(), is(instanceOf(ConstraintViolationException.class)));
            if (!checkNotNullArgumentViolationException((ConstraintViolationException) e.getCause())) {
                fail("Unexpected exception");
            }
        }
    }

    @Test
    @UsingDataSet(value = {DS_COURSE})
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testAddPersonToCourseForNotExistingPersonShouldThrowException() throws Exception {
        try {
            PersonVO notExistingPerson = createNotExistingPerson();
            personService.addPersonRequestForCourse(notExistingPerson, predefinedCourse);
            fail("Must throw exception");
        } catch (EJBException e) {
            assertThat(e.getCause(), is(instanceOf(ConstraintViolationException.class)));
            if (!checkNotNullArgumentViolationException((ConstraintViolationException) e.getCause())) {
                fail("Unexpected exception");
            }
        }
    }

    @Test
    @UsingDataSet(value = {DS_PERSON})
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON})
    public void testAddPersonToCourseForNullAsCourseShouldThrowException() throws Exception {
        try {
            personService.addPersonRequestForCourse(predefinedPerson, null);
            fail("Must throw exception");
        } catch (EJBException e) {
            assertThat(e.getCause(), is(instanceOf(ConstraintViolationException.class)));
            if (!checkNotNullArgumentViolationException((ConstraintViolationException) e.getCause())) {
                fail("Unexpected exception");
            }
        }
    }

    @Test
    @UsingDataSet(value = {DS_COURSE})
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testAddPersonToCourseForNullAsPersonShouldThrowException() throws Exception {
        try {
            personService.addPersonRequestForCourse(null, predefinedCourse);
            fail("Must throw exception");
        } catch (EJBException e) {
            assertThat(e.getCause(), is(instanceOf(ConstraintViolationException.class)));
            if (!checkNotNullArgumentViolationException((ConstraintViolationException) e.getCause())) {
                fail("Unexpected exception");
            }
        }
    }

    @Test
    @UsingDataSet(value = {DS_COURSE, DS_PERSON})
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE, DS_PERSON, DS_COURSE_PERSON})
    public void testAddPersonToCourseForNotValidCourseShouldAddCorrectly() throws Exception {
        CourseVO existingButNotValidCourse = new CourseVO();
        existingButNotValidCourse.setId(predefinedCourse.getId());
        personService.addPersonRequestForCourse(predefinedPerson, existingButNotValidCourse);
    }

    @Test
    @UsingDataSet(value = {DS_COURSE, DS_PERSON})
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE, DS_PERSON, DS_COURSE_PERSON})
    public void testAddPersonToCourseForNotValidPersonShouldAddCorrectly() throws Exception {
        PersonVO existingButNotValidPerson = new PersonVO();
        existingButNotValidPerson.setId(predefinedPerson.getId());
        personService.addPersonRequestForCourse(existingButNotValidPerson, predefinedCourse);
    }

    @Test
    @UsingDataSet(value = {DS_PERSON, DS_COURSE, DS_COURSE_PERSON})
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON, DS_COURSE})
    public void testRemovePersonFromCourse() throws Exception {
        personService.removePersonRequestForCourse(predefinedPerson, predefinedCourse);
    }

    @Test
    @UsingDataSet(value = {DS_PERSON, DS_COURSE})
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON, DS_COURSE})
    public void testRemovePersonFromCourseForNotInvolvedPerson() throws Exception {
        personService.removePersonRequestForCourse(predefinedPerson, predefinedCourse);
    }

    @Test
    @UsingDataSet(value = {DS_COURSE})
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE})
    public void testRemovePersonFromCourseForNotExistingPerson() throws Exception {
        PersonVO notExistingPerson = createNotExistingPerson();
        try {
            personService.removePersonRequestForCourse(notExistingPerson, predefinedCourse);
            fail("Must throw exception");
        } catch (EJBException e) {
            assertThat(e.getCause(), is(instanceOf(ConstraintViolationException.class)));
            if (!checkNotNullArgumentViolationException((ConstraintViolationException) e.getCause())) {
                fail("Unexpected exception");
            }
        }
    }

    @Test
    @UsingDataSet(value = {DS_PERSON})
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON})
    public void testRemovePersonFromCourseForNotExistingCourse() throws Exception {
        CourseVO notExistingCourse = createNotExistingCourse();
        try {
            personService.removePersonRequestForCourse(predefinedPerson, notExistingCourse);
            fail("Must throw exception");
        } catch (EJBException e) {
            assertThat(e.getCause(), is(instanceOf(ConstraintViolationException.class)));
            if (!checkNotNullArgumentViolationException((ConstraintViolationException) e.getCause())) {
                fail("Unexpected exception");
            }
        }
    }

    @Test
    public void testGetPersonsByRole() throws Exception {
        final int predefinedPersonsCnt = 1;
        List<PersonVO> personsByRole = personService.getPersonsByRole(predefinedPerson.getPersonRole());
        assertThat(personsByRole, hasItem(predefinedPerson));
        assertThat(personsByRole.size(), is(predefinedPersonsCnt));
    }

    @Test
    public void testGetPersonsByRoleForNullAsRoleShouldThrowException() throws Exception {
        try {
            personService.getPersonsByRole(null);
            fail("Must throw exception");
        } catch (EJBException e) {
            assertThat(e.getCause(), is(instanceOf(ConstraintViolationException.class)));
            if (!checkNotNullArgumentViolationException((ConstraintViolationException) e.getCause())) {
                fail("Unexpected exception");
            }
        }
    }

    @Test
    @UsingDataSet(value = {DS_PERSON, DS_COURSE, DS_COURSE_PERSON, DS_PRACTICE})
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON, DS_COURSE, DS_COURSE_PERSON, DS_PRACTICE, DS_MARK}, excludeColumns = {"id"})
    public void testSetMark() throws Exception {
        personService.setMark(predefinedPerson, predefinedPractice, new MarkVO(predefinedMarkToSave));
    }

    @Test
    @UsingDataSet(value = {DS_COURSE, DS_PRACTICE})
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE, DS_PRACTICE})
    public void testSetMarkForNotExistingPersonShouldThrowException() throws Exception {
        try {
            personService.setMark(createNotExistingPerson(), predefinedPractice, new MarkVO(predefinedMarkToSave));
            fail("Must throw exception");
        } catch (EJBException e) {
            return;
        }
    }

    @Test
    @UsingDataSet(value = {DS_COURSE, DS_PERSON})
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_COURSE, DS_PERSON})
    public void testSetMarkForNotExistingPracticeShouldThrowException() throws Exception {
        try {
            personService.setMark(predefinedPerson, createNotExistingPractice(), new MarkVO(predefinedMarkToSave));
            fail("Must throw exception");
        } catch (EJBException e) {
            return;
        }
    }

    @Test
    @UsingDataSet(value = {DS_PERSON, DS_COURSE, DS_COURSE_PERSON, DS_PRACTICE, DS_MARK})
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON, DS_COURSE, DS_COURSE_PERSON, DS_PRACTICE})
    public void testRemoveMark() throws Exception {
        personService.removeMark(predefinedMark);
    }

    @Test
    @UsingDataSet(value = {DS_PERSON, DS_COURSE, DS_COURSE_PERSON, DS_PRACTICE})
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON, DS_COURSE, DS_COURSE_PERSON, DS_PRACTICE})
    public void testRemoveMarkForNotExistingMarkShouldThrowException() throws Exception {
        try {
            personService.removeMark(createNotExistingMark());
            fail("Must throw exception");
        } catch (EJBException e) {
            return;
        }
    }

    @Test
    @UsingDataSet(value = {DS_PERSON, DS_COURSE, DS_COURSE_PERSON, DS_PRACTICE, DS_MARK})
    public void testGetMarks() throws Exception {
        List<MarkVO> marks = personService.getMarks(predefinedPerson, predefinedCourse);
        assertThat(marks.size(), is(predefinedMarksCnt));
        assertThat(marks, hasItem(predefinedMark));
    }

    @Test
    @UsingDataSet(value = {DS_PERSON, DS_COURSE, DS_COURSE_PERSON, DS_PRACTICE, DS_MARK})
    public void testGetMarksForNotExistingPersonShouldThrowException() throws Exception {
        try {
            personService.getMarks(createNotExistingPerson(), predefinedCourse);
            fail("Must throw exception");
        } catch (EJBException e) {
            return;
        }
    }

    private MarkVO createNotExistingMark() {
        return new MarkVO(0);
    }

    private PracticeLessonVO createNotExistingPractice() {
        PracticeLessonVO practiceLessonVO = new PracticeLessonVO(null, "practiceTask");
        return practiceLessonVO;
    }

    private PersonVO createNotExistingPerson() {
        PersonVO notExistingPerson = new PersonVO(null, "name", "lastName", "person@gmail.com", PersonRole.STUDENT);
        notExistingPerson.setSecondName("secondName");
        return notExistingPerson;
    }

    private CourseVO createNotExistingCourse() {
        return new CourseVO(null, "courseName", "courseDescription");
    }

    private boolean checkNotNullArgumentViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        return !(constraintViolations.size() != 1 ||
                !constraintViolations.iterator().next().getMessage().equals("may not be null"));
    }

    private boolean checkValidPersonViolationException(ConstraintViolationException e) {
        final int expectedViolationsCnt = 3;
        final Set<String> expectedViolationMessages = Collections.unmodifiableSet(new HashSet<>(Collections.nCopies(expectedViolationsCnt, "may not be null")));
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        if (constraintViolations.size() != expectedViolationsCnt) {
            return false;
        }
        Set<String> violationMessages = constraintViolations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
        return violationMessages.equals(expectedViolationMessages);
    }

}