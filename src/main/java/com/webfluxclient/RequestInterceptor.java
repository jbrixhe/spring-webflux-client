package com.webfluxclient;

import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ClientRequest;

@FunctionalInterface
public interface RequestInterceptor {

    ClientRequest process(ClientRequest request);

    default RequestInterceptor andThen(RequestInterceptor after) {
        Assert.notNull(after, "");
        return request -> after.process(process(request));
    }
}
