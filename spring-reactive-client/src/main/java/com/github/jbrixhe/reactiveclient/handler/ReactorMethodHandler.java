package com.github.jbrixhe.reactiveclient.handler;

import com.github.jbrixhe.reactiveclient.request.RequestTemplate;

public class ReactorMethodHandler implements MethodHandler {

    private RequestTemplate requestTemplate;

    public ReactorMethodHandler(RequestTemplate requestTemplate) {
        this.requestTemplate = requestTemplate;
    }

    @Override
    public Object invoke(Object[] args) {
        return null;
    }
}
