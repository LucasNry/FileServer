package org.example.fileserver.controller.get;

import annotations.ExposeHeaders;
import annotations.GetOperation;
import annotations.QueryParameter;
import annotations.RequestHeader;
import model.ConnectionType;
import model.Headers;
import model.HttpResponse;
import model.RequestStatus;
import org.example.fileserver.model.dao.FakeDbDAO;
import org.example.fileserver.model.ChunkRange;
import org.example.fileserver.model.FileFormat;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public class GetChunkController {
    static final String BYTES = "bytes";

    private static final String AUDIO_TAG_RANGE_SEPARATOR = "=";

    static final String ACCEPT_RANGES_HEADER_KEY = "Accept-Ranges";
    static final String CONTENT_RANGE_HEADER_KEY = "Content-Range";

    static final String RANGE_HEADER_KEY = "Range";
    static final String LENGTH_HEADER_KEY = "Length";

    static final String CONTENT_RANGE_TEMPLATE = "bytes %s-%s/%s";
    static final String RESOURCES_FOLDER_PATH_TEMPLATE = "resources/%s%s";

    private FakeDbDAO fakeDbDAO = new FakeDbDAO();

    @GetOperation(endpoint = "/stream/song")
    public HttpResponse getChunkAudioTag(@RequestHeader(RANGE_HEADER_KEY) String range, @QueryParameter("id") String songId) throws Exception {
        JSONObject songInfo = fakeDbDAO.getSongById(songId);
        String fileName = (String) songInfo.get("filename");

        try (FileInputStream fileInputStream = new FileInputStream(
                new File(String.format(RESOURCES_FOLDER_PATH_TEMPLATE, fileName, FileFormat.MP3.getExtension()))
        )) {
            Headers headers = new Headers(
                    ACCEPT_RANGES_HEADER_KEY, BYTES,
                    Headers.CONTENT_TYPE, "audio/mpeg",
                    Headers.CONNECTION, ConnectionType.KEEP_ALIVE.getValue()
            );
            int fileSize = fileInputStream.available();

            if (range == null) {
                return HttpResponse
                        .builder()
                        .headers(headers)
                        .requestStatus(RequestStatus.OK)
                        .build();
            }

            String rangeString = range.split(AUDIO_TAG_RANGE_SEPARATOR)[1]; // Splits "bytes=x-y" and returns the actual range
            ChunkRange chunkRange = new ChunkRange(rangeString, fileSize);
            byte[] audioChunk = getAudioChunk(fileInputStream, chunkRange);

            headers.addHeader(CONTENT_RANGE_HEADER_KEY, String.format(CONTENT_RANGE_TEMPLATE, chunkRange.getFrom(), chunkRange.getTo() - 1, fileSize));
            headers.addHeader(Headers.CONTENT_LENGTH, String.valueOf(audioChunk.length));

            return HttpResponse
                    .builder()
                    .requestStatus(RequestStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .body(convertByteArrayToString(audioChunk))
                    .build();
        }
    }

    @GetOperation(endpoint = "/song")
    @ExposeHeaders(keys = {RANGE_HEADER_KEY, LENGTH_HEADER_KEY})
    public HttpResponse getChunk(@RequestHeader(RANGE_HEADER_KEY) String range, @QueryParameter("id") String songId) throws Exception {
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
