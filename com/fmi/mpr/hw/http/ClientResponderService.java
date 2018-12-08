package com.fmi.mpr.hw.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

public class ClientResponderService extends Thread {

    private Socket socket;

    public ClientResponderService(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try(InputStream inputStream = socket.getInputStream()) {
            byte[] buffer = new byte[8192];
            int count;
            StringBuilder sb = new StringBuilder();
            while((count = inputStream.read(buffer)) > 0) {
                sb.append(new String(Arrays.copyOfRange(buffer, 0, count)));
            }
            HttpRequest request = new HttpRequest(sb.toString());
        } catch (IOException e) {
            throw new RuntimeException(e); //TODO
        }
    }
}
