package com.ericsson.automotive.common.json2json.dao;

import com.ericsson.automotive.common.json2json.model.JsonTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

/**
 * Created by ejaiwng on 8/29/2017.
 */
@Repository
public class JsonTemplateRepository {
    @PersistenceContext
    private EntityManager entityManager;


    public void create(JsonTemplate jsonTemplate) {
        entityManager.persist(jsonTemplate);
    }


    public void update(JsonTemplate jsonTemplate) {
        entityManager.merge(jsonTemplate);
    }


    public Optional<JsonTemplate> getJsonTemplateByName(String name) {
        return Optional.ofNullable(entityManager.find(JsonTemplate.class, name));
    }

    public void delete(String templateName) {
        getJsonTemplateByName(templateName).ifPresent(t -> entityManager.remove(t));
    }
}