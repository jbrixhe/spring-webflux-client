package com.webfluxclient.client;

import com.webfluxclient.metadata.request.Request;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

public interface RequestExecutor {
    Mono<ClientResponse> execute(Request request);
}
