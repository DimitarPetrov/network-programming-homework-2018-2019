package com.fmi.mpr.hw.http;


import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private String version = "";
    private String status = "";
    private Map<String,String> headers;
    private  byte[] body;

    public HttpResponse() {
        headers = new HashMap<>();
        body = new byte[0];
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setHeader(String header, String content) {
        headers.put(header, content);
    }

    public String getHeader(String header) {
        return headers.get(header);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(version + " " + status + "\r\n");
        for(Map.Entry<String,String> entry : headers.entrySet()) {
            sb.append(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }
        sb.append("\r\n");
        return sb.toString();
    }
}
