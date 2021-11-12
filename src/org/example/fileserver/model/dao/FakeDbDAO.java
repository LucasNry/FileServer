package org.example.fileserver.model.dao;

import org.example.fileserver.util.JSONUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class FakeDbDAO {
    private String FAKE_DB_FILENAME = "songs";

    public synchronized JSONObject getSongById(String id) {
        try {
            JSONArray songList = getAllSongs();

            for (Object song : songList) {
                JSONObject jsonObject = (JSONObject) song;
                String songId = (String) jsonObject.get("id");

                if (songId.equals(id)) {
                    return jsonObject;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public synchronized JSONArray getAllSongs() {
        try {
            return JSONUtil.readJSONFile(FAKE_DB_FILENAME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
