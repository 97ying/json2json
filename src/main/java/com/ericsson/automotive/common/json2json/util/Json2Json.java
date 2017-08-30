package com.ericsson.automotive.common.json2json.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

/**
 * Created by ejaiwng on 8/14/2017.
 */
public final class Json2Json {

    private final static Logger LOGGER = LoggerFactory.getLogger(Json2Json.class);

    private final static String KEY_PATH = "path";
    private final static String KEY_AS = "as";
    private final static String EMPTY_JSON = "{}";

    private Json2Json() {
    }

    public static String transformJson(String inputJson, String template) {
        Object inputJsonObject = JSON.parse(inputJson);
        JSONObject jsonTemplateObject = JSON.parseObject(template);

        Optional<Object> output = transformJson(inputJsonObject, jsonTemplateObject);

        return output.map(o -> JSON.toJSONString(o)).orElse(EMPTY_JSON);
    }

    public static Optional<Object> transformJson(Object inputJsonObject, JSONObject template) {

        Object output = null;
        Object items = null;

        for (Map.Entry<String, Object> entry : template.entrySet()) {
            switch (entry.getKey()) {
                case KEY_PATH: {
                    String path = (String) entry.getValue();
                    path = path.startsWith(".") ? "$" : "$." + path;

                    try {
                        items = JSONPath.eval(inputJsonObject, path);
                    } catch (Exception e) {
                        LOGGER.warn("Invalid JSON path: {} for item: {}", path, JSON.toJSONString(inputJsonObject), e);
                    }
                    break;
                }
                case KEY_AS: {
                    if (items instanceof JSONArray) {
                        output = Optional.ofNullable(output).orElse(new JSONArray());

                        for (Object item : (JSONArray) items) {
                            JSONObject outputItem = new JSONObject();

                            for (Map.Entry<String, Object> templateEntry : ((JSONObject) template.get(KEY_AS)).entrySet()) {
                                outputItem.put(templateEntry.getKey(), Json2Json.getValue(item, templateEntry.getValue()));
                            }

                            ((JSONArray) output).add(outputItem);
                        }
                    } else if (items instanceof JSONObject) {
                        output = Optional.ofNullable(output).orElse(new JSONObject());

                        for (Map.Entry<String, Object> templateEntry : ((JSONObject) template.get(KEY_AS)).entrySet()) {
                            ((JSONObject) output).put(templateEntry.getKey(), Json2Json.getValue(items, templateEntry.getValue()));
                        }
                    }

                    break;
                }
                default: {
                    if (entry.getValue() instanceof JSONObject) {
                        output = Optional.ofNullable(output).orElse(new JSONObject());
                        ((JSONObject) output).put(entry.getKey(), Json2Json.getValue(inputJsonObject, entry.getValue()));
                    }
                }
            }
        }


        return Optional.ofNullable(output);
    }

    private static Object getValue(Object item, Object template) {
        Object value;

        if (template instanceof JSONObject) {
            value = Json2Json.transformJson(item, (JSONObject) template);
        } else if (template instanceof String) {
            String outputTemplate = (String) template;

            if (((JSONObject) item).containsKey(outputTemplate)) {
                value = ((JSONObject) item).get(outputTemplate);
            } else {
                String path = outputTemplate.equals(".") ? "$" : "$." + outputTemplate;
                Optional<Object> itemValue = Optional.empty();
                try {
                    itemValue = Optional.ofNullable(JSONPath.eval(item, path));
                } catch (Exception e) {
                    LOGGER.warn("Invalid JSON path: {} for item: {}", path, JSON.toJSONString(item), e);
                }
                value = itemValue.orElse(outputTemplate);
            }
        } else {
            value = template;
        }

        return value;
    }
}
