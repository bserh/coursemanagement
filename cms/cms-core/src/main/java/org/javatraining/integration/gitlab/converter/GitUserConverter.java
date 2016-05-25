package org.javatraining.integration.gitlab.converter;

import org.javatraining.integration.gitlab.api.model.GitLabUser;
import org.javatraining.integration.gitlab.exception.UserRequiredPropertiesIsNotComparable;
import org.javatraining.integration.gitlab.impl.GitLabNotificationServiceImpl;
import org.javatraining.model.PersonVO;
import org.javatraining.service.impl.PersonServiceImpl;

import javax.inject.Inject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * The project name is cms.
 * Created by sergey on 12.06.15 at 21:21.
 * For more information you should send mail to codedealerb@gmail.com
 */
public class GitUserConverter {
    @Inject
    private PersonServiceImpl personService;
    private String rootMail;
    private GitLabNotificationServiceImpl gitLabNotification;

    public GitUserConverter() {
    }

    public GitUserConverter(String rootMail) {
        this.rootMail = rootMail;
        gitLabNotification = new GitLabNotificationServiceImpl();
    }

    public GitLabUser convertPerson(PersonVO personVO) {
        GitLabUser entity = new GitLabUser();

        entity.setEmail(personVO.getEmail());
        String userName = personVO.getEmail().split("@")[0];
        entity.setUsername(userName);
        entity.setName(personVO.getName() + "_" + personVO.getLastName());
        try {
            String password = generatePassword(userName);
            entity.setPassword(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String text = ""; //FIXME destroy template
        gitLabNotification.sendNotificationToEndPoint(rootMail, text, entity);

        return entity;
    }

    public PersonVO convertGitLabUserEntity(GitLabUser userEntity) {
        return personService.getByEmail(userEntity.getEmail());
    }

    public PersonVO mergePersons(PersonVO p1, PersonVO p2) throws UserRequiredPropertiesIsNotComparable {
        if (!p1.getId().equals(p2.getId())) {
            throw new UserRequiredPropertiesIsNotComparable("p1 isn\'t comparable with p2");
        }
        p1.setEmail(p2.getEmail());
        p1.setLastName(p2.getLastName());
        p1.setName(p2.getName());
        p1.setPersonRole(p2.getPersonRole());
        p1.setSecondName(p2.getSecondName());

        return p1;
    }

    public Collection<PersonVO> convertAllEntities(Collection<GitLabUser> entities) {
        return entities.stream().map(
                this::convertGitLabUserEntity
        ).collect(
                Collectors.toCollection(TreeSet::new)
        );
    }

    private String generatePassword(String userName) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(userName.getBytes());
        StringBuilder sb = new StringBuilder();

        for (byte aResult : result) {
            sb.append(Integer.toString((aResult & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString().substring(0, 10);//RETRIEVE FIRST 10 SYMBOLS FROM SHA1(USERNAME)
    }
}
