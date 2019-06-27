package com.automotive.common.json2json.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by ejaiwng on 8/29/2017.
 */
@Entity
@Table(name = "JsonTemplate")
public class JsonTemplate {

    @Id
    private String name;

    @Column(name = "template", columnDefinition = "TEXT")
    private String template;

    public JsonTemplate() {}

    public JsonTemplate(String name, String template) {
        this.name = name;
        this.template = template;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
