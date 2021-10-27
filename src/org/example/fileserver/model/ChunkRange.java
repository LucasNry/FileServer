package org.example.fileserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ChunkRange {

    private static final String TO_STRING_TEMPLATE = "%s-%s";
    private static final String RANGE_SEPARATOR = "-";

    private int from;

    private int to;

    public ChunkRange(String rangeHeaderValue) {
        String[] rangeStringSplit = rangeHeaderValue.split(RANGE_SEPARATOR);

        from = Integer.parseInt(rangeStringSplit[0]);
        to = Integer.parseInt(rangeStringSplit[1]);
    }

    @Override
    public String toString() {
        return String.format(TO_STRING_TEMPLATE, from, to);
    }

    public int getLength() {
        return to - from;
    }
}
