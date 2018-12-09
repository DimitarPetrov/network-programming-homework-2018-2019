package com.fmi.mpr.hw.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    public static final int PORT = 8080;

    public static void main(String[] args) {
        try(ServerSocket ss = new ServerSocket(PORT)) {

            while(true) {
                Socket clientSocket = ss.accept();
                new ClientResponderService(clientSocket).start();
            }

        } catch (IOException e) {
            System.err.println("Error starting the server:" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
