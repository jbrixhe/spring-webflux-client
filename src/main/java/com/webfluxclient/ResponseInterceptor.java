package com.webfluxclient;

import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;

@FunctionalInterface
public interface ResponseInterceptor {

    ClientResponse process(ClientResponse clientResponse);

    default ResponseInterceptor andThen(ResponseInterceptor after) {
        Assert.notNull(after, "");
        return request -> after.process(process(request));
    }
}
