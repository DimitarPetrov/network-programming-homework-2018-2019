package com.fmi.mpr.hw.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private HttpMethod method;
    private String URL;
    private String version;
    private Map<String, String> headers;
    private String body;

    public HttpRequest(HttpMethod method,String URL, String version, Map<String,String> headers, String body) {
        this.method = method;
        this.URL = URL;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public HttpRequest(String request) {
        String[] lines = request.split("\n");
        String[] firstHeader = lines[0].split(" ");
        HttpMethod method = HttpMethod.valueOf(firstHeader[0].trim());
        String URL = firstHeader[1];
        String version = firstHeader[2];
        Map<String,String> headers = new HashMap<>();
        int i = 1;
        for(;i < lines.length; ++i) {
            if(lines[i].split(":").length == 1) {
                break;
            }
            String[] header = lines[i].split(":");
            headers.put(header[0].trim(), String.join(":", Arrays.copyOfRange(header, 1, header.length)).trim());
        }
        ++i;
        StringBuilder body = new StringBuilder();
        for(; i < lines.length; ++i) {
            body.append(lines[i]);
        }
        this.method = method;
        this.URL = URL;
        this.version = version;
        this.headers = headers;
        this.body = body.toString();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(method + " " + URL + " " + version + "\n");
        for(Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey() + ": " + entry.getValue() + "\n");
        }
        sb.append("\n");
        sb.append(body);
        return sb.toString();
    }
}
