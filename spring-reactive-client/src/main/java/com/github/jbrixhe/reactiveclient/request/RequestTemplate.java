package com.github.jbrixhe.reactiveclient.request;

import org.springframework.http.HttpMethod;

public class RequestTemplate {

    private RequestPath requestPath;
    private HttpMethod method;

    public RequestTemplate(RequestTemplate requestTemplate) {
        this.requestPath = new RequestPath(requestTemplate.getRequestPath());
    }

    public RequestTemplate() {
        this.requestPath = new RequestPath();
    }

    public RequestPath getRequestPath() {
        return requestPath;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }
}
