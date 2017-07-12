package com.webfluxclient;

import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ClientRequest;

@FunctionalInterface
public interface RequestProcessor {

    ClientRequest process(ClientRequest request);

    default RequestProcessor andThen(RequestProcessor after) {
        Assert.notNull(after, "");
        return request -> after.process(process(request));
    }
}
