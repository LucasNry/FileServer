package org.example.fileserver.controller.get;

import annotations.GetOperation;
import model.HttpResponse;
import org.example.fileserver.model.dao.FakeDbDAO;
import org.json.simple.JSONArray;

public class GetSongsController {

    private FakeDbDAO fakeDbDAO = new FakeDbDAO();

    @GetOperation(endpoint = "/songs")
    public HttpResponse getAllSongs() {
        JSONArray jsonArray = fakeDbDAO.getAllSongs();

        return HttpResponse
                .builder()
                .body(jsonArray.toJSONString())
                .build();
    }
}
