package org.example.fileserver.util;

import java.io.InputStream;

public class ResourceLoader {
    private static final String RESOURCES_PATH_TEMPLATE = "%s";
//    private static final String RESOURCES_PATH_TEMPLATE = "resources/%s";

    public static InputStream getResourcePath(String resourceName) {
        return ResourceLoader
                .class
                .getClassLoader()
                .getResourceAsStream(String.format(RESOURCES_PATH_TEMPLATE, resourceName));
    }
}
