package org.example.fileserver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum FileFormat {
    MP3(".mp3"),
    JSON(".json");

    @Getter
    private String extension;
}
