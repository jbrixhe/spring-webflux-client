package com.webfluxclient;

import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ClientResponse;

@FunctionalInterface
public interface ResponseProcessor {

    ClientResponse process(ClientResponse clientResponse);

    default ResponseProcessor andThen(ResponseProcessor after) {
        Assert.notNull(after, "");
        return request -> after.process(process(request));
    }
}
