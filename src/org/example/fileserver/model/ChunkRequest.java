package org.example.fileserver.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChunkRequest {
    private static final String SEPARATOR = " ";

    private int videoId;

    private int startRange;

    private int endRange;

    @Override
    public String toString() {
        return videoId +
                SEPARATOR +
                startRange +
                SEPARATOR +
                endRange;
    }
}
