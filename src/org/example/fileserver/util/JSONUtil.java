package org.example.fileserver.util;

import org.example.fileserver.model.FileFormat;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;

public class JSONUtil {
    private static final String FAKE_DB_PATH_TEMPLATE = "fakeDB/%s%s";

    private static JSONParser jsonParser = new JSONParser();

    public static synchronized JSONArray readJSONFile(String fileName) throws Exception {
        try (FileReader reader = new FileReader(String.format(FAKE_DB_PATH_TEMPLATE, fileName, FileFormat.JSON.getExtension()))) {
            return (JSONArray) ((JSONObject) jsonParser.parse(reader)).get(fileName);
        }
    }

    public static synchronized void writeJSONToFile(String fileName, JSONArray jsonArray) throws Exception {
        try (FileWriter file = new FileWriter(String.format(FAKE_DB_PATH_TEMPLATE, fileName, FileFormat.JSON.getExtension()))) {
            file.write(jsonArray.toJSONString());
            file.flush();
        }
    }
}
