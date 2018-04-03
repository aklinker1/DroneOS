package com.klinker.droneos.utils.io;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.klinker.droneos.utils.random.RandomString;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsonFileTest {

    @Test
    public void read() throws Exception {
        String path = "src/test/resources/JsonFile_read.json";
        TextFile textFile = new TextFile(path);
        String jsonString = textFile.read();
        JsonElement expected = new JsonParser().parse(jsonString);

        JsonFile jsonFile = new JsonFile(path);
        JsonElement actual = jsonFile.read();

        assertEquals(expected, actual);
    }

    @Test
    public void write() throws Exception {
        RandomString random = new RandomString(5);
        String expectedJsonString = "{\n"
                + "\"" + random.nextString() + "\": {"
                + "\"arg1\": \"" + random.nextString() + "\""
                + "}"
                + "}";
        JsonParser parser = new JsonParser();
        JsonElement expected = parser.parse(expectedJsonString);

        JsonFile jsonFile = new JsonFile("src/test/resources/JsonFile_write.json");
        jsonFile.write(expected);
        JsonElement actual = jsonFile.read();

        assertEquals(expected, actual);
    }

}