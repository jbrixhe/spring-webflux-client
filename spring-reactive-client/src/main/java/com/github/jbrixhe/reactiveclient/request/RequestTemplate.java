package com.github.jbrixhe.reactiveclient.request;

public class RequestTemplate {

    private RequestPath requestPath;

    public RequestTemplate(RequestTemplate requestTemplate) {
        this.requestPath = new RequestPath(requestTemplate.getRequestPath());
    }

    public RequestTemplate() {
        this.requestPath = new RequestPath();
    }

    public RequestPath getRequestPath() {
        return requestPath;
    }

}
