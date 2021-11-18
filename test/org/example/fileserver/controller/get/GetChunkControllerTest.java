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
import java.io.FileOutputStream;
import java.io.FileWriter;
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
                        String.format("resources/%s%s", "bensound-tomorrow", FileFormat.MP3.getExtension())
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

        ChunkRange currentRange = new ChunkRange("0-", fileLength);
        do {
            HttpResponse response = getChunkController.getChunk(String.format("%s-", currentRange.getFrom()), EXPECTED_SONG_ID);
            byte[] chunk = getChunkFromBody(response.getBody());
            byteArrayOutputStream.write(chunk);

            Headers responseHeaders = response.getHeaders();
            ChunkRange lastRange = new ChunkRange(responseHeaders.getHeader(GetChunkController.RANGE_HEADER_KEY), fileLength);
            currentRange = new ChunkRange(
                    String.format("%s-", lastRange.getTo() + 1),
                    fileLength
            );
            System.out.println(currentRange);
        } while (currentRange.getFrom() != fileLength);

        Assert.assertArrayEquals(expectedAudioFileInBytes, byteArrayOutputStream.toByteArray());
    }

    private byte[] getChunkFromBody(String base64EncodedBody) {
        return Base64
                .getDecoder()
                .decode(base64EncodedBody);
    }

}
