package com.github.jbrixhe.reactiveclient.request;

import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

public class RequestTemplate {

    Map<String, HeaderTemplate> headerTemplates;
    private RequestPath requestPath;
    HttpMethod method;

    public RequestTemplate(RequestTemplate requestTemplate) {
        this();
        this.requestPath.getSegments().addAll(requestTemplate.requestPath.getSegments());
        this.headerTemplates.putAll(requestTemplate.headerTemplates);
        this.method = requestTemplate.method;

    }

    public RequestTemplate() {
        this.requestPath = new RequestPath();
        this.headerTemplates = new HashMap<>();
    }

    public RequestPath getRequestPath() {
        return requestPath;
    }

    public void addHeader(String name, String value) {
        if (!headerTemplates.containsKey(name)) {
            headerTemplates.put(name, new HeaderTemplate.BasicT(name, value));
        }
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }
}
