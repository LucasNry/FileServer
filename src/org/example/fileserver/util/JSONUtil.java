package org.example.fileserver.util;

import org.example.fileserver.model.FileFormat;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JSONUtil {
    private static final String FILENAME_TEMPLATE = "%s%s"; // Related to jar

    private static JSONParser jsonParser = new JSONParser();

    public static synchronized JSONArray readJSONFile(String fileName) throws Exception {
        try (InputStreamReader reader = new InputStreamReader(
                ResourceLoader.getResourcePath(
                        String.format(FILENAME_TEMPLATE, fileName, FileFormat.JSON.getExtension())
                )
        )) {
            return (JSONArray) ((JSONObject) jsonParser.parse(reader)).get(fileName);
        }
    }
}
