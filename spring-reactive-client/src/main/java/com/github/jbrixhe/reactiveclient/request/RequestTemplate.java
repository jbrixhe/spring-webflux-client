package com.github.jbrixhe.reactiveclient.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class RequestTemplate {

    private RequestPath requestPath;

    public RequestTemplate(RequestTemplate requestTemplate) {
        this.requestPath = new RequestPath();
    }

    public RequestTemplate() {
        this.requestPath = new RequestPath();
    }

    public RequestPath getRequestPath() {
        return requestPath;
    }

    public void addHeader(String name) {

    }

    public void addHeader(String name, Object value) {

    }

    private String urlEncode(Object arg) {
        try {
            return URLEncoder.encode(String.valueOf(arg), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
