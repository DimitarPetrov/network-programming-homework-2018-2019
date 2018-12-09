package com.fmi.mpr.hw.http;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClientResponderService extends Thread {

    private Socket socket;

    public ClientResponderService(Socket socket) {
        this.socket = socket;
    }

    private HttpResponse respond(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        response.setVersion(request.getVersion());
        if(request.getMethod() == HttpMethod.GET)  {
            if(Files.notExists(Paths.get("/repository" + request.getURL()))) {
                response.setStatus("404 Not Found");
                return response;
            }
        }
        return response;
    }

    @Override
    public void run() {
        try(BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            OutputStream outputStream = socket.getOutputStream()) {
            StringBuilder sb = new StringBuilder();
            String line;
            while(!(line = br.readLine()).equals("")) {
                sb.append(line + "\n");
            }
            HttpRequest request = new HttpRequest(sb.toString());
            HttpResponse response = respond(request);
            outputStream.write(response.toString().getBytes("UTF-8"));
            outputStream.flush();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e); //TODO
        }
    }
}
