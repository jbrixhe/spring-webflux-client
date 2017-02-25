package com.reactiveclient.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

interface HttpExceptionReader {

    boolean canRead(HttpStatus httpStatus);

    <T> Flux<T> read(ClientHttpResponse inputMessage);

    <T> Mono<T> readMono(ClientHttpResponse inputMessage);
}
