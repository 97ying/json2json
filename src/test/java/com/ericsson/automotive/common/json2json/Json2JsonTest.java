package com.ericsson.automotive.common.json2json;

import com.ericsson.automotive.common.json2json.tomcat.util.Json2Json;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by ejaiwng on 8/25/2017.
 */
public class Json2JsonTest {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(Json2JsonTest.class);

    @Test
    public void testComplexJson2Json() throws Exception {

        String input = getFileContent("original.json");
        String template = getFileContent("template.json");
        String target = getFileContent("target.json");

        String output = Json2Json.transformJson(input, template);
        LOGGER.info(output);
        JSONAssert.assertEquals(target, output, true);
    }

    private String getFileContent(String fileInClassPath) throws URISyntaxException, IOException {
        Path path = Paths.get(ClassLoader.getSystemResource(fileInClassPath).toURI());
        return new String(Files.readAllBytes(path));
    }

    @Test
    public void testInvalidPathFunction() throws JSONException {
        String input = "{\n" +
                "   \"key2\": \"value2\"\n" +
                "}";
        String template = "{\n" +
                "  \"path\": \".\",\n" +
                "  \"as\": {\n" +
                "    \"key1\": \"key2[]\"\n" +
                "  }\n" +
                "}";

        String output = Json2Json.transformJson(input, template);
        LOGGER.info(output);
        JSONAssert.assertEquals("{\"key1\":\"key2[]\"}", output, true);
    }

    @Test
    public void testStringObjectMapping() throws JSONException {
        String input = "{\n" +
                "   \"key2\": \"value2\"\n" +
                "}";
        String template = "{\n" +
                "  \"path\": \".\",\n" +
                "  \"as\": {\n" +
                "    \"key1\": \"string\"\n" +
                "  }\n" +
                "}";

        String output = Json2Json.transformJson(input, template);
        LOGGER.info(output);
        JSONAssert.assertEquals("{\"key1\":\"string\"}", output, true);
    }

    @Test
    public void testSubObjectMapping() throws JSONException {
        String input = "{\n" +
                "   \"key2\":{\n" +
                "      \"subkey\":\"subvalue\"\n" +
                "   }\n" +
                "}";
        String template = "{\n" +
                "  \"path\": \".\",\n" +
                "  \"as\": {\n" +
                "    \"key1\": \"key2.subkey\"\n" +
                "  }\n" +
                "}";

        String output = Json2Json.transformJson(input, template);
        LOGGER.info(output);
        JSONAssert.assertEquals("{\"key1\":\"subvalue\"}", output, true);
    }

    @Test
    public void testOutputAtSecondLevelObject() throws JSONException {
        String input = "{" +
                "   \"data1\":{" +
                "      \"key2\":\"value2\"" +
                "   }" +
                "}";
        String template = "{\n" +
                "  \"path\": \"data1\",\n" +
                "  \"as\": {\n" +
                "    \"key1\": \"key2\"\n" +
                "  }\n" +
                "}";

        String output = Json2Json.transformJson(input, template);
        LOGGER.info(output);
        JSONAssert.assertEquals("{\"key1\":\"value2\"}", output, true);
    }

    @Test
    public void testTwoOutputsAtFirstLevel() throws JSONException {
        String input = "[{'key2': 'value2'},{'key2': 'value3'}]";
        String template = "{" +
                "  'level1': {" +
                "    'path': '.'," +
                "    'as': {" +
                "      'key1': 'key2'" +
                "    }" +
                "  }," +
                "  'level2': {" +
                "    'path': '.'," +
                "    'as': {" +
                "      'key1': 'key2'" +
                "    }" +
                "  }" +
                "}";

        String output = Json2Json.transformJson(input, template);
        LOGGER.info(output);
        JSONAssert.assertEquals("{\"level1\":[{\"key1\":\"value2\"},{\"key1\":\"value3\"}],\"level2\":[{\"key1\":\"value2\"},{\"key1\":\"value3\"}]}", output, true);
    }

    @Test
    public void testSecondOutputLevelArray() throws JSONException {
        String input = "[{\"key2\": \"value2\"},{\"key2\": \"value3\"}]";
        String template = "{" +
                "  \"level1\": {" +
                "    \"path\": \".\"," +
                "    \"as\": {" +
                "      \"key1\": \"key2\"" +
                "    }" +
                "  }" +
                "}";

        String output = Json2Json.transformJson(input, template);
        LOGGER.info(output);
        JSONAssert.assertEquals("{\"level1\":[{\"key1\":\"value2\"},{\"key1\":\"value3\"}]}", output, true);
    }

    @Test
    public void testFirstLevelArrayWithOneElement() throws JSONException {
        String input = "[{\"key2\": \"value2\"}]";
        String template = "{" +
                "            \"path\" : \".\"," +
                "            \"as\" : {" +
                "                \"key1\" : \"key2\"" +
                "            }" +
                "          }";

        String output = Json2Json.transformJson(input, template);
        LOGGER.info(output);
        JSONAssert.assertEquals("[{\"key1\":\"value2\"}]", output, true);
    }

    @Test
    public void testFirstLevelObject() throws JSONException {

        String input = "{\"key2\": \"value2\"}";

        String template = "{" +
                "  \"path\": \".\"," +
                "  \"as\": {" +
                "    \"_key2\": \"key2\"" +
                "  }" +
                "}";

        String output = Json2Json.transformJson(input, template);
        LOGGER.info(output);
        JSONAssert.assertEquals("{\"_key2\":\"value2\"}", output, true);
    }

}
