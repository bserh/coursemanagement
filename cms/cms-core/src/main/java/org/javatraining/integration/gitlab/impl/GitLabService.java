package org.javatraining.integration.gitlab.impl;

import org.javatraining.integration.gitlab.api.interfaces.GitLabAPIClient;
import org.javatraining.integration.gitlab.api.model.*;
import org.javatraining.integration.gitlab.converter.GitUserConverter;
import org.javatraining.integration.gitlab.exception.ResourceNotFoundException;
import org.javatraining.integration.gitlab.exception.UserRequiredPropertiesIsNotComparable;
import org.javatraining.model.PersonVO;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.core.Response;
import java.util.Collection;

/**
 * The project name is cms.
 * Created by sergey on 09.06.15 at 16:03.
 * For more information you should send mail to codedealerb@gmail.com
 */
public class GitLabService {
    private static final String ROOT = "root";
    private GitLabSessionParameters params;
    private GitLabAPIClient gitLabClient;
    private String pToken;

    public GitLabService(String host, String root_login, String root_pass, String root_email) {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(host);

        setParams(new GitLabSessionParameters(root_login, root_pass, root_email));
        this.gitLabClient = target.proxy(GitLabAPIClient.class);
        this.pToken = getPrivateToken();
    }

    public Collection<PersonVO> getAllPersons() {
        return new GitUserConverter().convertAllEntities(gitLabClient.getAllUsers(pToken, ROOT));
    }

    public PersonVO getPerson(String email) {
        GitLabUser user = gitLabClient.getUser(pToken, ROOT, email).get(0);

        return new GitUserConverter().convertGitLabUserEntity(user);
    }

    public boolean addPerson(PersonVO personVO) {
        Response.Status status = gitLabClient.createUser(pToken, ROOT, new GitUserConverter(params.getEmail()).convertPerson(personVO));

        return status.getStatusCode() == 201;
    }


    public boolean updatePerson(PersonVO personVO) {
        PersonVO person = new GitUserConverter().convertGitLabUserEntity(
                gitLabClient.getUser(pToken, ROOT, personVO.getEmail()).get(0));

        try {
            person = new GitUserConverter().mergePersons(person, personVO);
            Response.Status status = gitLabClient.updateUser(pToken, ROOT, new GitUserConverter().convertPerson(person));
            return status.getStatusCode() == 200;//FIXME check 200 or 201
        } catch (UserRequiredPropertiesIsNotComparable e) {
            System.err.println(e.getMessage());
        }

        return false;
    }

    public void removePerson(PersonVO personVO) {
        PersonVO gitLabUserVO = getPerson(personVO.getEmail());
        gitLabClient.removeUser(pToken, ROOT, gitLabUserVO.getId());
    }

    public boolean createProject(PersonVO personVO) {
        GitLabProject defaultProject = getDefaultProject();
        PersonVO gitLabUserVO = getPerson(personVO.getEmail());
        Response.Status status = gitLabClient.createProject(pToken, ROOT, gitLabUserVO.getId(), defaultProject);

        return status.getStatusCode() == 201;
    }

    public Collection<GitLabProject> getAllProjects() {
        return gitLabClient.getAllProjects(pToken, ROOT);
    }

    public boolean addProjectMember(PersonVO personVO, Integer projId) throws ResourceNotFoundException {
        GitLabProjectMember projectMember =
                (GitLabProjectMember) new GitUserConverter().convertPerson(personVO);
        projectMember.setAccessLevel(GitLabAccessLevel.Reporter);
        Response.Status status = gitLabClient.addProjectTeamMember(pToken, ROOT, projectMember, projId);

        if (status.getStatusCode() != 201) {
            throw new ResourceNotFoundException("user or project not found");
        }

        return true;
    }

    public boolean removeProjectMember(PersonVO personVO, GitLabProject project) throws ResourceNotFoundException {
        PersonVO gitLabEntityVO = getPerson(personVO.getEmail());
        GitLabProjectMember projectMember =
                (GitLabProjectMember) new GitUserConverter().convertPerson(gitLabEntityVO);

        Response.Status status = gitLabClient.removeProjectTeamMember(pToken, ROOT, project.getId(), personVO.getId());

        if (status.getStatusCode() != 200) {
            throw new ResourceNotFoundException("user or project not found");
        }

        return true;
    }

    public GitLabSessionParameters getParams() {
        return params;
    }

    public void setParams(GitLabSessionParameters params) {
        this.params = params;
    }

    private String getPrivateToken() {
        GitLabSession session = gitLabClient.getSession(params, "root");
        return session.getPrivateToken();
    }

    private GitLabProject getDefaultProject() {
        GitLabProject projectEntity = new GitLabProject();

        projectEntity.setDescription("Personal project");
        projectEntity.setIssuesEnabled(true);
        projectEntity.setMergeRequestsEnabled(false);
        projectEntity.setName("Personal");
        projectEntity.setVisibilityLevel(GitLabProject.VISIBILITY_PRIVATE_LEVEL);
        projectEntity.setPublic(false);
        projectEntity.setWikiEnabled(true);
        projectEntity.setSnippetsEnabled(true);

        return projectEntity;
    }
}
