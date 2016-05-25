package org.javatraining.integration.gitlab.api.impl;

import org.javatraining.entity.enums.PersonRole;
import org.javatraining.integration.gitlab.api.model.GitLabProject;
import org.javatraining.integration.gitlab.exception.ResourceNotFoundException;
import org.javatraining.integration.gitlab.impl.GitLabService;
import org.javatraining.model.PersonVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * The project name is cms.
 * Created by sergey on 12.06.15 at 21:38.
 * For more information you should send mail to codedealerb@gmail.com
 */

@Ignore
public class GitLabServiceTest {
    private GitLabService service;
    private PersonVO testPerson, testPerson2;

    @Before
    public void setUpGitlabClient() {
        service = new GitLabService("http://localhost", "root", "12345678", "admin@example.com");
        testPerson = new PersonVO((long) 2, "testName", "testSurname", "testUserMail@mail.ru", PersonRole.STUDENT);
        testPerson2 = new PersonVO((long) 3, "testName2", "testSurname2", "testUserMail2@mail.ru", PersonRole.TEACHER);
    }

    @After
    public void closeClient() {
        service = null;
    }

    @Test
    public void testGetAllPersonsFromGitLab() {
        assertNotNull(service.getAllPersons());
        assertFalse(service.getAllPersons().isEmpty());
        assertTrue(service.getAllPersons().size() >= 1);
    }

    @Test
    public void testGetTestPersonFromGitLab() {
        service.addPerson(testPerson);//create person
        PersonVO test = service.getPerson(testPerson.getEmail());//retrieve person

        assertNotNull(test);
        assertTrue(test.equals(testPerson));
        assertTrue(test.getId().equals(testPerson.getId()));

        service.removePerson(testPerson);
    }

    @Test
    public void testGetResultOfCreatingAndDeletingUser() {
        assertTrue(service.addPerson(testPerson));//return 201 http code if created
        service.removePerson(testPerson);
        assertTrue(service.getPerson(testPerson.getEmail()) == null);
    }

    @Test
    public void testCreateProjectForTestUser() {
        if (service.getPerson(testPerson.getEmail()) == null) {
            service.addPerson(testPerson);
        }
        assertTrue(service.createProject(testPerson));
    }

    @Test
    public void testAddAndRemoveProjectMemberForTestUsersProject() {
        if (service.getPerson(testPerson.getEmail()) == null) {
            service.addPerson(testPerson2);
            GitLabProject project =
                    service.getAllProjects().stream().filter(
                            p -> p.getOwner().getEmail().equals(testPerson.getEmail())
                    ).findAny().get();
            if (project != null) {
                try {
                    assertTrue(service.addProjectMember(testPerson2, project.getId()));
                    assertTrue(service.removeProjectMember(testPerson2, project));
                } catch (ResourceNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
