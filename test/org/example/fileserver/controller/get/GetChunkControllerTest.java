package org.example.fileserver.controller.get;

import model.Headers;
import model.HttpResponse;
import org.example.fileserver.model.ChunkRange;
import org.example.fileserver.model.FileFormat;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Base64;

public class GetChunkControllerTest {

    private static final String EXPECTED_SONG_ID = "1";

    private byte[] expectedAudioFileInBytes;

    private GetChunkController getChunkController = new GetChunkController();

    @Before
    public void before() throws Exception {
        if (expectedAudioFileInBytes == null) {
            expectedAudioFileInBytes = getAudioFileInBytes();
        }
    }

    private byte[] getAudioFileInBytes() throws Exception {
        FileInputStream fileInputStream = new FileInputStream(
                new File(
                        String.format(GetChunkController.RESOURCES_FOLDER_PATH_TEMPLATE, "bensound-tomorrow", FileFormat.MP3.getExtension())
                )
        );

        byte[] result = new byte[fileInputStream.available()];
        fileInputStream.read(result);

        return result;
    }

    @Test
    public void testStreamAudio() throws Exception {
        HttpResponse httpResponse = getChunkController.getChunk(null, EXPECTED_SONG_ID);
        Headers headers = httpResponse.getHeaders();
        int fileLength = Integer.parseInt(headers.getHeader(GetChunkController.LENGTH_HEADER_KEY));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(fileLength);

        ChunkRange currentRange = new ChunkRange("0-1000000", fileLength);
        do {
            HttpResponse response = getChunkController.getChunk(currentRange.toString(), EXPECTED_SONG_ID);
            byte[] chunk = getChunkFromBody(response.getBody());
            System.out.println(response.getBody());
            System.out.println();
            System.out.println(Arrays.toString(chunk));
            byteArrayOutputStream.write(chunk);

            Headers responseHeaders = response.getHeaders();
            ChunkRange lastRange = new ChunkRange(responseHeaders.getHeader(GetChunkController.RANGE_HEADER_KEY), fileLength);
            currentRange = new ChunkRange(
                    lastRange.getTo(),
                    Math.min((lastRange.getTo() + 1000000), fileLength)
            );
        } while (byteArrayOutputStream.size() != fileLength);

        Assert.assertArrayEquals(expectedAudioFileInBytes, byteArrayOutputStream.toByteArray());
    }

    private byte[] getChunkFromBody(String base64EncodedBody) {
        return Base64
                .getDecoder()
                .decode(base64EncodedBody);
    }

}
