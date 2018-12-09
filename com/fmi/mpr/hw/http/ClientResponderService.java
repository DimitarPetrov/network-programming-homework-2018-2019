package com.fmi.mpr.hw.http;

import javax.lang.model.type.UnknownTypeException;
import java.io.*;
import java.net.FileNameMap;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ClientResponderService extends Thread {

    private static final String REPOSITORY_PATH = "repository";

    private static final Map<String, String> videoMimeTypes;

    static {
        videoMimeTypes = new HashMap<>();
        videoMimeTypes.put(".flv", "video/x-flv");
        videoMimeTypes.put(".mp4", "video/mp4");
        videoMimeTypes.put(".m3u8", "application/x-mpegURL");
        videoMimeTypes.put(".ts", "video/MP2T");
        videoMimeTypes.put(".3gp", "video/3gpp");
        videoMimeTypes.put(".mov", "video/quicktime");
        videoMimeTypes.put(".avi", "video/x-msvideo");
        videoMimeTypes.put(".wmv", "video/x-ms-wmv");
    }

    private Socket socket;

    public ClientResponderService(Socket socket) {
        this.socket = socket;
    }

    private void sendFile(Path filePath, String contentType, HttpResponse response) throws IOException {
        response.setHeader("Content-type", contentType);
        byte[] file = Files.readAllBytes(filePath);
        response.setHeader("Content-length", Integer.valueOf(file.length).toString());
        response.setBody(file);
        response.setStatus("200 OK");
    }

    private HttpResponse serveGetRequest(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        response.setVersion(request.getVersion());
        Path filePath = Paths.get(REPOSITORY_PATH + request.getURL());

        if (Files.notExists(filePath)) {
            response.setStatus("404 Not Found");
            return response;
        }

        //String type = Files.probeContentType(filePath); // Best solution but does not work on mac-os.

        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String type = fileNameMap.getContentTypeFor(filePath.toString()); // For some reason do not work for videos.

        if (type != null && type.startsWith("image")) {
            sendFile(filePath, type, response);
            return response;
        }

        if (type != null && type.startsWith("text")) {
            sendFile(filePath, type, response);
            return response;
        }

        if (filePath.toString().lastIndexOf('.') != -1 && (type = videoMimeTypes.get(filePath.toString().substring(filePath.toString().lastIndexOf('.')))) != null) {
            sendFile(filePath, type, response);
            return response;
        }

        throw new UnknownTypeException(null, null);
    }

    private HttpResponse servePostRequest(HttpRequest request) {
        return null;
    }

    private HttpResponse serveRequest(HttpRequest request) throws IOException {

        if (request.getMethod() == HttpMethod.GET) {
            return serveGetRequest(request);
        } else if (request.getMethod() == HttpMethod.POST) {
            return servePostRequest(request);
        }

        return null;
    }

    private HttpRequest extractRequest(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while (!(line = br.readLine()).equals("")) {
            sb.append(line + "\n");
        }
        return new HttpRequest(sb.toString());
    }

    private void sendResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        outputStream.write(response.toString().getBytes());
        outputStream.write(response.getBody());
        outputStream.flush();
    }


    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             OutputStream outputStream = socket.getOutputStream()) {

            HttpRequest request = extractRequest(br);
            HttpResponse response = serveRequest(request);

            sendResponse(outputStream, response);

            socket.close();
        } catch (IOException e) {
            System.err.println("I/O Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
