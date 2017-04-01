package com.reactiveclient;

import com.reactiveclient.metadata.request.ReactiveRequest;

@FunctionalInterface
public interface RequestInterceptor {
    void accept(ReactiveRequest reactiveRequest);
}
