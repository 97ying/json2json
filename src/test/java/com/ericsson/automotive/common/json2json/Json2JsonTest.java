package com.ericsson.automotive.common.json2json;

import com.ericsson.automotive.common.json2json.tomcat.util.Json2Json;
import org.junit.Test;

/**
 * Created by ejaiwng on 8/25/2017.
 */
public class Json2JsonTest {

    @Test
    public void testSecondOutputLevelArray() {
        String input = "";
        String template = "";
        System.out.println(Json2Json.transformJson(input, template));
    }

    @Test
    public void testFirstLevelArrayWithOneElement() {
        String input = "[{\"key2\": \"value2\"}]";
        String template = "{" +
                "            \"path\" : \".\"," +
                "            \"as\" : {" +
                "                \"key1\" : \"key2\"" +
                "            }" +
                "          }";

        System.out.println(Json2Json.transformJson(input, template));
    }

    @Test
    public void testFirstLevelObject() {

        String input = "{\"key2\": \"value2\"}";

        String template = "{" +
                "  \"path\": \".\"," +
                "  \"as\": {" +
                "    \"_key2\": \"key2\"" +
                "  }" +
                "}";

        System.out.println(Json2Json.transformJson(input, template));
    }

}
