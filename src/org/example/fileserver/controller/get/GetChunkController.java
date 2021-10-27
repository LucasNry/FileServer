package org.example.fileserver.controller.get;

import annotations.GetOperation;
import annotations.QueryParameter;
import annotations.RequestHeader;
import model.Headers;
import model.HttpResponse;
import model.RequestStatus;
import org.example.fileserver.dao.FakeDbDAO;
import org.example.fileserver.model.ChunkRange;
import org.example.fileserver.model.FileFormat;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public class GetChunkController {

    static final String RANGE_HEADER_KEY = "Range";
    static final String LENGTH_HEADER_KEY = "Length";
    static final String RESOURCES_FOLDER_PATH_TEMPLATE = "resources/%s%s";

    private FakeDbDAO fakeDbDAO = new FakeDbDAO();

    @GetOperation(endpoint = "/song")
    public HttpResponse getChunk(@RequestHeader(RANGE_HEADER_KEY) String range, @QueryParameter String songId) throws Exception {
        JSONObject songInfo = fakeDbDAO.getSongById(songId);
        String fileName = (String) songInfo.get("filename");

        try (FileInputStream fileInputStream = new FileInputStream(
                new File(String.format(RESOURCES_FOLDER_PATH_TEMPLATE, fileName, FileFormat.MP3.getExtension()))
        )) {
            Headers headers = new Headers(
                    LENGTH_HEADER_KEY, String.valueOf(fileInputStream.available()),
                    RANGE_HEADER_KEY, range
            );

            if (range == null) {

                return HttpResponse
                        .builder()
                        .headers(headers)
                        .requestStatus(RequestStatus.OK)
                        .build();
            }

            byte[] audioChunk = getAudioChunk(fileInputStream, new ChunkRange(range));
            return HttpResponse
                    .builder()
                    .requestStatus(RequestStatus.OK)
                    .headers(headers)
                    .body(convertByteArrayToString(audioChunk))
                    .build();
        }
    }

    private byte[] getAudioChunk(FileInputStream fileInputStream, ChunkRange chunkRange) throws IOException {
        byte[] result = new byte[chunkRange.getLength()];

        fileInputStream.skip(chunkRange.getFrom());
        fileInputStream.read(result);

        return result;
    }

    private String convertByteArrayToString(byte[] chunkByteArray) {
        return Base64
                .getEncoder()
                .encodeToString(chunkByteArray);
    }
}
