package org.javatraining.service.impl;

import org.javatraining.config.AuthRole;
import org.javatraining.dao.PersonDAO;
import org.javatraining.entity.PersonEntity;
import org.javatraining.entity.enums.PersonRole;
import org.javatraining.integration.google.oauth.GoogleUserinfoService;
import org.javatraining.integration.google.oauth.exception.AuthException;
import org.javatraining.service.AuthService;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

/**
 * Created by olga on 05.06.15.
 */
@ApplicationScoped
public class AuthServiceImpl implements AuthService {
    @Inject
    private GoogleUserinfoService googleUserinfoService;
    @EJB
    private PersonDAO personDAO;

    @Override
    public boolean isCredentialValid(@NotNull String idFromRequest, @NotNull String token) {
        String emailFromDB = getPersonEntityById(idFromRequest).getEmail();
        String emailFromToken = googleUserinfoService.getEmailByToken(token);
        return emailFromToken.equals(emailFromDB);
    }

    @Override
    public String getRoleById(@NotNull String idFromRequest) {
        PersonRole personRole = getPersonEntityById(idFromRequest).getPersonRole();
        switch (personRole) {
            case TEACHER:
                return AuthRole.TEACHER;
            case STUDENT:
                return AuthRole.STUDENT;
            default:
                throw new AuthException("Unsupported role from DB");
        }
    }

    private
    @NotNull
    PersonEntity getPersonEntityById(final String id) throws AuthException {
        PersonEntity personEntity = personDAO.getById(Long.valueOf(id));
        if (personEntity == null) {
            throw new AuthException(String.format("Person with id == %d doesn't exist in the DB", id));
        }
        return personEntity;
    }
}
