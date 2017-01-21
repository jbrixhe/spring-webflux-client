package com.github.jbrixhe.reactiveclient.handler;

import com.github.jbrixhe.reactiveclient.metadata.MethodMetadata;
import com.github.jbrixhe.reactiveclient.metadata.request.Request;

public class ReactorMethodHandler implements MethodHandler {

    private MethodMetadata methodMetadata;

    public ReactorMethodHandler(MethodMetadata methodMetadata) {
        this.methodMetadata = methodMetadata;
    }

    @Override
    public Object invoke(Object[] args) {
        Request request = methodMetadata.getRequestTemplate().apply(args);
        return "Test";
    }
}
