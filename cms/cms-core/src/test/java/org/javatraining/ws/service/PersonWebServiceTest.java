package org.javatraining.ws.service;

import flexjson.JSONSerializer;
import org.javatraining.auth.Auth;
import org.javatraining.config.Config;
import org.javatraining.dao.PersonDAO;
import org.javatraining.dao.exception.EntityNotExistException;
import org.javatraining.entity.PersonEntity;
import org.javatraining.entity.enums.PersonRole;
import org.javatraining.model.PersonVO;
import org.javatraining.model.conversion.PersonConverter;
import org.javatraining.service.PersonService;
import org.javatraining.service.exception.UnsupportedOperationException;
import org.javatraining.service.impl.PersonServiceImpl;
import org.javatraining.ws.config.ServiceConfig;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Ignore
@RunWith(Arquillian.class)
public class PersonWebServiceTest {

    private WebTarget target;

    private static PersonVO teacherPerson;
    private static PersonVO studentPerson;
    private static PersonVO newPerson;

    static {
        teacherPerson = new PersonVO(1L, "teacherName", "teacherLastName", "teacher@gmail.com", PersonRole.TEACHER);
        teacherPerson.setSecondName("teacherSecondName");
        studentPerson = new PersonVO(2L, "studentName", "studentLastName", "student@gmail.com", PersonRole.STUDENT);
        studentPerson.setSecondName("studentSecondName");
        newPerson = new PersonVO();
        newPerson.setName("newName");
        newPerson.setSecondName("newSecondName");
        newPerson.setLastName("newLastName");
        newPerson.setEmail("new@gmail.com");
    }

    private static String TEACHER_TOKEN = "AIzaSyDEVCJp5Hz_fSrHYeS24EcMM3FQV0GF8Do";
    private static String STUDENT_TOKEN = "AIzaSyDEVCJp5Hz_fSrHYeS24EcMM3FQV0GF8Do";


    @Deployment(testable = true)
    public static WebArchive createDeployment() {
        File[] artifact = Maven.resolver().loadPomFromFile("pom.xml")
                .resolve("net.sf.flexjson:flexjson").withoutTransitivity().asFile();

        return ShrinkWrap.create(WebArchive.class, "WebServicesTest.war")
                .addPackage(PersonEntity.class.getPackage())
                .addPackage(PersonVO.class.getPackage())
                .addPackage(PersonConverter.class.getPackage())
                .addPackage(PersonWebService.class.getPackage())
                .addPackage(PersonDAO.class.getPackage())
                .addPackage(PersonService.class.getPackage())
                .addPackage(PersonServiceImpl.class.getPackage())
                .addPackage(Auth.class.getPackage())
                .addPackage(EntityNotExistException.class.getPackage())
                .addPackage(PersonRole.class.getPackage())
                .addClass(ServiceConfig.class)
                .addClass(UnsupportedOperationException.class)
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
                .addAsLibraries(artifact);
    }

    @ArquillianResource
    private URL base;

    @Before
    public void setUp() throws MalformedURLException {
        Client client = ClientBuilder.newClient();
        target = client.target(URI.create(new URL(base, "resources/person").toExternalForm()));
        target.register(PersonVO.class);
    }

    @Test
    @UsingDataSet("datasets/person-web-service-test/initial-data.json")
    @InSequence(1)
    public void setupDB_ARQ1077_Workaround() {
        //ugly hack to fill db from dataset. for more information see https://issues.jboss.org/browse/ARQ-1077
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void testGetSamePersonByIdAsTeacher() {
        PersonVO predefinedPersonFromService = target.path("{id}")
                .resolveTemplate("id", teacherPerson.getId())
                .request(MediaType.APPLICATION_JSON)
                .header(Config.REQUEST_HEADER_ID, teacherPerson.getId())
                .header(Config.REQUEST_HEADER_TOKEN, TEACHER_TOKEN)
                .get(PersonVO.class);
        System.out.println(new JSONSerializer().prettyPrint(true).serialize(predefinedPersonFromService));
        System.out.println(new JSONSerializer().prettyPrint(true).serialize(teacherPerson));
        assertEquals(predefinedPersonFromService, teacherPerson);
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void testGetAnotherPersonByIdAsTeacher() {
        PersonVO predefinedPersonFromService = target.path("{id}")
                .resolveTemplate("id", studentPerson.getId())
                .request(MediaType.APPLICATION_JSON)
                .header(Config.REQUEST_HEADER_ID, teacherPerson.getId())
                .header(Config.REQUEST_HEADER_TOKEN, TEACHER_TOKEN)
                .get(PersonVO.class);
        assertEquals(predefinedPersonFromService, studentPerson);
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void testGetSamePersonByIdAsStudent() {
        PersonVO predefinedPersonFromService = target.path("{id}")
                .resolveTemplate("id", studentPerson.getId())
                .request(MediaType.APPLICATION_JSON)
                .header(Config.REQUEST_HEADER_ID, studentPerson.getId())
                .header(Config.REQUEST_HEADER_TOKEN, STUDENT_TOKEN)
                .get(PersonVO.class);
        assertEquals(predefinedPersonFromService, studentPerson);
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void testGetAnotherPersonByIdAsStudent() {
        Response response = target.path("{id}")
                .resolveTemplate("id", teacherPerson.getId())
                .request(MediaType.APPLICATION_JSON)
                .header(Config.REQUEST_HEADER_ID, studentPerson.getId())
                .header(Config.REQUEST_HEADER_TOKEN, STUDENT_TOKEN)
                .get();
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void testGetPersonByIdAsGuest() {
        Response response = target.path("{id}")
                .resolveTemplate("id", teacherPerson.getId())
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void testCreatePerson() {
        Response response = target.request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(
                        newPerson,
                        MediaType.APPLICATION_JSON
                ));
        assertNotNull(response.getLocation());
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void getPersonsByRoleAsStudent() {
        Response response = target.queryParam("role", "student")
                .request(MediaType.APPLICATION_JSON)
                .header(Config.REQUEST_HEADER_ID, studentPerson.getId())
                .header(Config.REQUEST_HEADER_TOKEN, STUDENT_TOKEN)
                .get();
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void getPersonsByRoleAsTeacher() {
        Response response = target.queryParam("role", "student")
                .request(MediaType.APPLICATION_JSON)
                .header(Config.REQUEST_HEADER_ID, teacherPerson.getId())
                .header(Config.REQUEST_HEADER_TOKEN, TEACHER_TOKEN)
                .get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void getPersonsByNonExistingRoleAsTeacher() {
        Response response = target.queryParam("role", "adventurer")
                .request(MediaType.APPLICATION_JSON)
                .header(Config.REQUEST_HEADER_ID, teacherPerson.getId())
                .header(Config.REQUEST_HEADER_TOKEN, TEACHER_TOKEN)
                .get();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }
}