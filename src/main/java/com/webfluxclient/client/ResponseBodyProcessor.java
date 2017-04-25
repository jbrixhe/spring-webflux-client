package com.webfluxclient.client;

import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

public interface ResponseBodyProcessor {
    Object process(Mono<ClientResponse> clientResponse);
}
