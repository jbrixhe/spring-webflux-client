package com.webfluxclient;

import com.webfluxclient.metadata.request.Request;
import org.springframework.util.Assert;

@FunctionalInterface
public interface RequestInterceptor {

    void accept(Request request);

    default RequestInterceptor andThen(RequestInterceptor after) {
        Assert.notNull(after, "");
        return request -> { accept(request); after.accept(request);};
    }

}
