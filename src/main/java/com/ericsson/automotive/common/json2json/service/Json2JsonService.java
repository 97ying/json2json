/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ericsson.automotive.common.json2json.service;

import com.ericsson.automotive.common.json2json.model.JsonTemplate;
import com.ericsson.automotive.common.json2json.dao.JsonTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional
public class Json2JsonService {

    @Autowired
    private JsonTemplateRepository jsonTemplateRepository;

    public JsonTemplate create(String name, String jsonBody) {
        JsonTemplate jsonTemplate = constructJsonTemplate(name, jsonBody);
        jsonTemplateRepository.create(jsonTemplate);
        return jsonTemplate;
    }

    public JsonTemplate update(String name, String jsonBody) {
        JsonTemplate jsonTemplate = constructJsonTemplate(name, jsonBody);
        jsonTemplateRepository.update(jsonTemplate);
        return jsonTemplate;
    }

    private JsonTemplate constructJsonTemplate(String name, String jsonBody) {
        JsonTemplate jsonTemplate = new JsonTemplate();
        jsonTemplate.setName(name);
        jsonTemplate.setTemplate(jsonBody);
        return jsonTemplate;
    }

    public boolean delete(String name) {
        jsonTemplateRepository.delete(name);
        return true;
    }

    public Optional<JsonTemplate> get(String name) {
        return jsonTemplateRepository.getJsonTemplateByName(name);
    }

}
