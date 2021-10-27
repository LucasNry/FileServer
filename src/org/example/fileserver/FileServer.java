package org.example.fileserver;

import controller.ServerController;

import java.io.IOException;

public class FileServer extends ServerController {
    private static final int PORT = 9876;

    public FileServer() throws IOException {
        super(PORT);
    }

    public static void main(String[] args) throws Exception {
        FileServer fileServer = new FileServer();
        fileServer.start();
    }
}
