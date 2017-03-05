package com.reactiveclient;

import com.reactiveclient.metadata.request.Request;

@FunctionalInterface
public interface RequestInterceptor {
    void accept(Request request);
}
