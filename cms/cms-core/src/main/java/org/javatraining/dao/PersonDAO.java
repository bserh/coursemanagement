package org.javatraining.dao;


import org.javatraining.dao.exception.EntityNotExistException;
import org.javatraining.entity.PersonEntity;
import org.javatraining.entity.enums.PersonRole;

import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;
import java.util.List;

@Stateless
public class PersonDAO extends GenericDAO<PersonEntity> {

    public PersonDAO() {
        setEntityClass(PersonEntity.class);
    }

    public List<PersonEntity> getAllPersons() {
        Query query = getEntityManager().createQuery("SELECT c FROM PersonEntity c");
        return query.getResultList();
    }

    public PersonEntity getByEmail(@NotNull String email) {
        Query query = getEntityManager().createQuery("SELECT c FROM PersonEntity c WHERE c.email =:email ").setParameter("email", email);
        List<PersonEntity> resultList = query.getResultList();
        if (resultList.size() == 0) {
            throw new EntityNotExistException();
        }
        return resultList.get(0);
    }

    public List<PersonEntity> getByPersonRole(@NotNull PersonRole personRole) {
        Query query = getEntityManager().createQuery("SELECT c FROM PersonEntity c WHERE c.personRole =:personRole ").setParameter("personRole", personRole);
        return query.getResultList();
    }

    public void clear() {
        getEntityManager().createQuery("delete from PersonEntity").executeUpdate();
    }


}
