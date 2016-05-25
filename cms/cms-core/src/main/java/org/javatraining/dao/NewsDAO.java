package org.javatraining.dao;


import org.javatraining.entity.NewsEntity;

import javax.ejb.Stateless;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class NewsDAO extends GenericDAO<NewsEntity> {

    public NewsDAO() {
        setEntityClass(NewsEntity.class);
    }


    public List<NewsEntity> getAllNews() {
        Query query = getEntityManager().createQuery("SELECT c FROM NewsEntity c");
        return query.getResultList();
    }

}
