package com.webfluxclient.client;

import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

public interface ResponseBodyProcessor<T> {
    T process(Mono<ClientResponse> clientResponse);
}
