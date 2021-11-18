package org.example.fileserver.controller.get;

import annotations.ExposeHeaders;
import annotations.GetOperation;
import annotations.QueryParameter;
import annotations.RequestHeader;
import model.Headers;
import model.HttpResponse;
import model.RequestStatus;
import org.example.fileserver.model.dao.FakeDbDAO;
import org.example.fileserver.model.ChunkRange;
import org.example.fileserver.model.FileFormat;
import org.example.fileserver.util.ResourceLoader;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class GetChunkController {
    static final String CONTENT_RANGE_HEADER_KEY = "Content-Range";

    static final String RANGE_HEADER_KEY = "Range";
    static final String LENGTH_HEADER_KEY = "Length";

    static final String SONG_FILENAME_TEMPLATE = "%s%s";

    private FakeDbDAO fakeDbDAO = new FakeDbDAO();

    @GetOperation(endpoint = "/song")
    @ExposeHeaders(keys = {RANGE_HEADER_KEY, LENGTH_HEADER_KEY})
    public HttpResponse getChunk(@RequestHeader(RANGE_HEADER_KEY) String range, @QueryParameter("id") String songId) throws Exception {
        JSONObject songInfo = fakeDbDAO.getSongById(songId);
        String fileName = (String) songInfo.get("filename");

        try (InputStream fileInputStream = ResourceLoader.getResourcePath(
                String.format(SONG_FILENAME_TEMPLATE, fileName, FileFormat.MP3.getExtension())
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

            ChunkRange chunkRange = new ChunkRange(range, fileInputStream.available());
            byte[] audioChunk = getAudioChunk(fileInputStream, chunkRange);

            headers.addHeader(RANGE_HEADER_KEY, chunkRange.toString());

            return HttpResponse
                    .builder()
                    .requestStatus(RequestStatus.OK)
                    .headers(headers)
                    .body(convertByteArrayToString(audioChunk))
                    .build();
        }
    }

    private byte[] getAudioChunk(InputStream fileInputStream, ChunkRange chunkRange) throws IOException {
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
