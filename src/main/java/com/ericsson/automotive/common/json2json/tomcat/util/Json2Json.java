package com.ericsson.automotive.common.json2json.tomcat.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;

import java.util.Map;

/**
 * Created by ejaiwng on 8/14/2017.
 */
public class Json2Json {

    private final static String KEY_PATH = "path";
    private final static String KEY_AS = "as";

    public static String transformJson(String inputJson, String template) {
        Object inputJsonObject = JSON.parse(inputJson);
        JSONObject jsonTemplateObject = JSON.parseObject(template);

        Object output = transformArray(inputJsonObject, jsonTemplateObject);

        return JSON.toJSONString(output);
    }

    public static Object transformArray(Object inputJsonObject, JSONObject template) {

        Object output = null;
        Object items = null;
        for (Map.Entry<String, Object> entry : template.entrySet()) {
            switch (entry.getKey()) {
                case KEY_PATH: {
                    String path = (String)entry.getValue();
                    path = path.startsWith(".") ? "$" : "$." + path;

                    items = JSONPath.eval(inputJsonObject, path);
                    break;
                }
                case KEY_AS: {
                    if (items instanceof JSONArray) {
                        output = new JSONArray();

                        for(Object item : (JSONArray)items) {
                            JSONObject outputItem = new JSONObject();

                            for(Map.Entry<String, Object> templateEntry : ((JSONObject)template.get(KEY_AS)).entrySet()) {
                                outputItem.put(templateEntry.getKey(), Json2Json.getValue(item, templateEntry.getValue()));
                            }

                            ((JSONArray)output).add(outputItem);
                        }



                    } else if (items instanceof JSONObject) {
                        output = new JSONObject();

                        for(Map.Entry<String, Object> templateEntry : ((JSONObject)template.get(KEY_AS)).entrySet()) {
                            ((JSONObject)output).put(templateEntry.getKey(), Json2Json.getValue(items, templateEntry.getValue()));
                        }
                    }

                    break;
                }
                default: {
                    if (entry.getValue() instanceof JSONArray) {
                        output = new JSONObject();
                        ((JSONObject)output).put(entry.getKey(), Json2Json.getValue(inputJsonObject, entry.getValue()));
                    }
                }
            }
        }


        return output;
    }

    public static Object getValue(Object item, Object template) {
        Object value = null;

        if (template instanceof JSONObject && ((JSONObject)template).get(KEY_PATH) != null && ((JSONObject)template).get(KEY_AS) != null) {
            Json2Json.transformArray(item, (JSONObject) template);
        } else if (template instanceof String){
            String outputTemplate = (String) template;

            if (((JSONObject)item).containsKey(outputTemplate)) {
                value = ((JSONObject)item).get(outputTemplate);
            } else {
                String path = outputTemplate.equals(".") ? "$" : "$." + outputTemplate;

                Object itemValue = JSONPath.eval(item, path);

                if (itemValue != null) {
                    value = itemValue;
                } else {
                    value = outputTemplate;
                }
            }
        } else {
            value = template;
        }

        return value;
    }
}
