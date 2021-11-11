package org.example.fileserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ChunkRange {

    private static final int DEFAULT_CHUNK_SIZE = 15000;

//    private static final String TO_STRING_TEMPLATE = "bytes=%s-%s";
    private static final String TO_STRING_TEMPLATE = "%s-%s";
    private static final String RANGE_SEPARATOR = "-";

    private int from;

    private int to;

    public ChunkRange(String rangeHeaderValue, int fileLength) {
        String[] rangeStringSplit = rangeHeaderValue.split(RANGE_SEPARATOR);

        from = Integer.parseInt(rangeStringSplit[0]);
        if (rangeStringSplit.length > 1) {
            to = Integer.parseInt(rangeStringSplit[1]);
        } else {
            to = Math.min(from + DEFAULT_CHUNK_SIZE, fileLength);
        }
    }

    @Override
    public String toString() {
        return String.format(TO_STRING_TEMPLATE, from, to - 1);
    }

    public int getLength() {
        return to - from;
    }
}
