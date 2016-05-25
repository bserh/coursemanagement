package org.javatraining.dao;

import org.javatraining.dao.exception.EntityIsAlreadyExistException;
import org.javatraining.dao.exception.EntityNotExistException;
import org.javatraining.entity.PersonEntity;
import org.javatraining.entity.enums.PersonRole;
import org.javatraining.entity.util.Pair;
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

import static org.assertj.core.api.StrictAssertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;

/**
 * Created by vika on 12.06.15.
 */

@RunWith(Arquillian.class)
@UsingDataSet(value = "dao-tests/person/person.json")
@Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
public class PersonDAOTest {

    @EJB
    PersonDAO personDAO;

    private static final String DS_DIR = "dao-tests/";
    private static final String DS_EMPTY = DS_DIR + "empty.json";

    private static final String DS_PERSON = DS_DIR + "person/person.json";
    private static final String DS_PERSON_AFTER_UPDATE = DS_DIR + "person/expected-after-update.json";
    private static final String DS_PERSON_AFTER_SAVE = DS_DIR + "person/expected-after-save.json";


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
    public void testPersonDAOShouldBeInjected() throws Exception {
        assertThat(personDAO, is(notNullValue()));
    }


    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON_AFTER_SAVE}, excludeColumns = {"id", "phone"})
    public void testSave() {
        PersonEntity personForSaving = personEntityInitializeForTests(new PersonEntity());
        personDAO.save(personForSaving);
    }

    @Test
    public void testGetPersonByEmail() {
        PersonEntity personEntity = personDAO.save(personEntityInitializeForTests(new PersonEntity()));
        PersonEntity otherPersonEntity = personEntityInitializeForTests(new PersonEntity());
        otherPersonEntity.setEmail("OtherEmeil@gmail.com");
        personDAO.save(otherPersonEntity);
        assertEquals(personDAO.getByEmail(personEntity.getEmail()), personEntity);
        assertEquals(personDAO.getByEmail(otherPersonEntity.getEmail()), otherPersonEntity);
    }

    @Test
    public void testSaveReturnPersonEntity() {
        PersonEntity personEntity = new PersonEntity();
        assertEquals(personEntity, personDAO.save(personEntityInitializeForTests(personEntity)));
    }

    @Test
    public void testUpdateReturnPersonEntity() {
        PersonEntity personEntity = personEntityInitializeForTests(new PersonEntity());
        personDAO.save(personEntity);
        personEntity.setName("Student");
        assertEquals(personDAO.update(personEntity), personEntity);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON_AFTER_UPDATE}, excludeColumns = {"id", "phone"})
    public void testUpdatePerson() {
        PersonEntity personEntity = predefinedPersonInitializationForTests();
        personEntity.setName("Student");
        assertEquals(personDAO.update(personEntity), personEntity);
    }

    @Test
    public void testGetPersonEntity() {
        PersonEntity predefinedPerson = predefinedPersonInitializationForTests();
        assertThat(predefinedPerson, is(equalTo(predefinedPerson)));
    }

    @Test
    @ShouldMatchDataSet(value = DS_EMPTY)
    public void testRemovePerson() {
        personDAO.remove(predefinedPersonInitializationForTests());
    }

    @Test
    @ShouldMatchDataSet(value = DS_EMPTY)
    public void testClearDataBaseShouldBeEmpty() {
        personDAO.clear();
    }


    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON}, excludeColumns = {"id", "phone"})
    public void testGetAllPersons() {
        assertThat(personDAO.getAllPersons(), is(notNullValue()));

    }

    @Test(expected = EJBException.class)
    public void testUpdateNotValidPersonEntityTrowEJBException() {
        personDAO.update(new PersonEntity());
    }

    @Test(expected = Exception.class)
    public void testUpdateNullPersonEntityTrowEJBException() {
        personDAO.update(null);
    }

    @Test(expected = EJBException.class)
    public void testSaveNotValidPersonEntityTrowEJBException() {
        personDAO.save(new PersonEntity());
    }

    @Test(expected = Exception.class)
    public void testSaveNullPersonEntityTrowEJBException() {
        personDAO.save(null);
    }

    @Test(expected = EJBException.class)
    public void testSRemoveNotValidPersonEntityTrowEJBException() {
        personDAO.remove(new PersonEntity());
    }

    @Test(expected = Exception.class)
    public void testRemoveNullPersonEntityTrowEJBException() {
        personDAO.remove(null);
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON})
    public void testGetByIdForNotExistingIdShouldReturnEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        assertThatThrownBy(() -> personDAO.getById(notExistingId))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with " + notExistingId + " does not exist in database");

    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON})
    public void testRemoveByIdForNotExistingIdShouldReturnEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        assertThatThrownBy(() -> personDAO.removeById(notExistingId))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON})
    public void testSavePersonThatExistThrowsEntityIsAlreadyExistException() throws EntityIsAlreadyExistException {
        PersonEntity personThatExists = predefinedPersonInitializationForTests();
        assertThatThrownBy(() -> personDAO.save(personThatExists))
                .isInstanceOf(EntityIsAlreadyExistException.class).hasMessage("Field with id = "
                + personThatExists.getId() + " already exists in database");
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON})
    public void testUpdatePersonThatNotExistThrowsEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        PersonEntity personThatNotExists = predefinedPersonInitializationForTests();
        personThatNotExists.setId(notExistingId);
        assertThatThrownBy(() -> personDAO.update(personThatNotExists))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }

    @Test
    @ShouldMatchDataSet(value = {DS_EMPTY, DS_PERSON})
    public void testRemovePersonThatNotExistThrowsEntityNotExistException() throws EntityNotExistException {
        Long notExistingId = 10L;
        PersonEntity personThatExists = predefinedPersonInitializationForTests();
        personThatExists.setId(notExistingId);
        assertThatThrownBy(() -> personDAO.remove(personThatExists))
                .isInstanceOf(EntityNotExistException.class).hasMessage("Field with "
                + notExistingId + " does not exist in database");
    }


    public PersonEntity personEntityInitializeForTests(PersonEntity personEntity) {
        personEntity.setName("Petro");
        personEntity.setEmail("Petrovg@mail.ru");
        personEntity.setPhone("0933122345");
        personEntity.setLastName("Last Name");
        personEntity.setSecondName("Second name");
        personEntity.setPersonRole(PersonRole.TEACHER);
        return personEntity;
    }

    public PersonEntity predefinedPersonInitializationForTests() {
        PersonEntity personEntity = new PersonEntity();
        personEntity.setId(1L);
        personEntity.setName("teacherName");
        personEntity.setLastName("teacherLastName");
        personEntity.setEmail("teacher@gmail.com");
        personEntity.setSecondName("teacherSecondName");
        personEntity.setPersonRole(PersonRole.TEACHER);
        return personEntity;
    }


}
