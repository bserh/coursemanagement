package org.javatraining.dao;

import org.javatraining.dao.exception.EntityIsAlreadyExistException;
import org.javatraining.dao.exception.EntityNotExistException;
import org.javatraining.entity.GenericEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;


public abstract class GenericDAO<ENTITY extends GenericEntity> {


    Class<ENTITY> entityClass;

    @PersistenceContext
    private EntityManager em;

    public GenericDAO() {
    }

    public void setEntityClass(Class<ENTITY> entityClass) {
        this.entityClass = entityClass;
    }


    public ENTITY remove(@NotNull ENTITY entity) {
        if (getEntityManager().find(entityClass, entity.getId()) == null) {
            throw new EntityNotExistException("Field with " + entity.getId() + " does not exist in database");
        } else {

            entity = getEntityManager().merge(entity);
            getEntityManager().remove(entity);
        }
        return entity;
    }

    public ENTITY getById(@NotNull Long id) {
        ENTITY entity = getEntityManager().find(entityClass, id);
        if (entity == null) {
            throw new EntityNotExistException("Field with " + id + " does not exist in database");
        }
        return entity;
    }

    public ENTITY removeById(@NotNull Long id) {
        ENTITY entity = getEntityManager().find(entityClass, id);
        if (entity == null) {
            throw new EntityNotExistException("Field with " + id + " does not exist in database");
        }
        getEntityManager().remove(entity);
        return entity;
    }

    public ENTITY save(@NotNull ENTITY entity) {
        if (entity.getId() != null && getEntityManager().find(entityClass, entity.getId()) != null) {
            throw new EntityIsAlreadyExistException("Field with id = "
                    + entity.getId()
                    + " already exists in database");
        } else {
            getEntityManager().persist(entity);
        }
        return entity;
    }

    public ENTITY update(@NotNull ENTITY entity) {
        if (entity.getId() != null && getEntityManager().find(entityClass, entity.getId()) == null) {
            throw new EntityNotExistException("Field with " + entity.getId() + " does not exist in database");
        } else {
            getEntityManager().merge(entity);
        }
        return entity;
    }


    protected EntityManager getEntityManager() {
        return em;
    }


}
