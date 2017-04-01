package com.reactiveclient;

import com.reactiveclient.metadata.request.ClientRequest;

@FunctionalInterface
public interface RequestInterceptor {
    void accept(ClientRequest clientRequest);
}
