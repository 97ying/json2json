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

package com.ericsson.automotive.common.json2json.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.ericsson.automotive.common.json2json.Exception.ServiceException;
import com.ericsson.automotive.common.json2json.model.JsonTemplate;
import com.ericsson.automotive.common.json2json.model.ResponseMessage;
import com.ericsson.automotive.common.json2json.service.Json2JsonService;
import com.ericsson.automotive.common.json2json.util.Json2Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/json2json")
public class Json2JsonController {

    private final static String INPUT_TEMPLATE_NAME = "template.name";
    private final static String INPUT_TEMPLATE_BODY = "template.body";
    private final static String INPUT_JSON_NAME = "inputJson";

    @Autowired
    private Json2JsonService json2JsonService;

    @RequestMapping(value = "/template/{name}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public String createJson2JsonTemplate(@PathVariable(value = "name") String name,
                                    @RequestBody String jsonBody) {
        return json2JsonService.create(name, jsonBody).getTemplate();
    }

    @RequestMapping(value = "/template/{name}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public String updateJson2JsonTemplate(@PathVariable(value = "name") String name,
                                          @RequestBody String jsonBody) {
        return json2JsonService.update(name, jsonBody).getTemplate();
    }

    @RequestMapping(value = "/template/{name}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getJson2JsonTemplate(@PathVariable(value = "name") String name) {
        return json2JsonService.get(name).map(t -> t.getTemplate()).orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Template '" + name + "' doesn't exist"));
    }

    @RequestMapping(value = "/template/{name}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deleteJson2JsonTemplate(@PathVariable(value = "name") String name) {
        json2JsonService.delete(name);
    }

    @RequestMapping(value = "/transformation",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST)
    @ResponseBody
    public String json2Json(@RequestBody String jsonBody) {

        JSONObject jsonBodyObject = (JSONObject) JSON.parse(jsonBody);
        Optional<String> templateName = Optional.ofNullable((String) JSONPath.eval(jsonBodyObject, INPUT_TEMPLATE_NAME));
        Optional<JSONObject> templateBody = Optional.ofNullable((JSONObject) JSONPath.eval(jsonBodyObject, INPUT_TEMPLATE_BODY));
        Optional<Object> inputJson = Optional.ofNullable(JSONPath.eval(jsonBodyObject, INPUT_JSON_NAME));

        return inputJson.map(
                input -> templateBody.map(
                        t -> Json2Json.transformJson(input, t))
                        .map(o -> JSON.toJSONString(o))
                        .orElse(
                                templateName.map(
                                        name -> json2JsonService.get(name)
                                                .map(t -> t.getTemplate())
                                                .map(s -> Json2Json.transformJson(JSON.toJSONString(input), s))
                                                .orElseThrow(() -> new ServiceException(HttpStatus.BAD_REQUEST.value(), "Template '" + name + "' doesn't exist"))
                                ).orElseThrow(() -> new ServiceException(HttpStatus.BAD_REQUEST.value(), "Field 'name' under 'template' is empty"))
                        )).orElseThrow(() -> new ServiceException(HttpStatus.BAD_REQUEST.value(), "Field 'inputJson' is empty"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseMessage> handleException(Exception e) {

        if (e instanceof ServiceException) {
            ServiceException serviceException = (ServiceException) e;
            ResponseMessage responseMessage = new ResponseMessage(serviceException.getStatus(), serviceException.getErrorMessage());
            return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(serviceException.getStatus()));
        } else {
            ResponseMessage responseMessage = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
            return new ResponseEntity<>(responseMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
