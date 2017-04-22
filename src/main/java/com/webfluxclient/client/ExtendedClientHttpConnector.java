package com.webfluxclient.client;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.client.reactive.ReactorClientHttpRequest;
import org.springframework.http.client.reactive.ReactorClientHttpResponse;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClient;

import java.net.URI;
import java.util.function.Function;

class ExtendedClientHttpConnector implements ClientHttpConnector {

    private final HttpClient httpClient;

    public ExtendedClientHttpConnector() {
        this.httpClient = HttpClient.create();
    }

    @Override
    public Mono<ClientHttpResponse> connect(HttpMethod method, URI uri, Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {

        return httpClient
                .request(io.netty.handler.codec.http.HttpMethod.valueOf(method.name()),
                        uri.toString(),
                        httpClientRequest -> requestCallback.apply(new ReactorClientHttpRequest(method, uri, httpClientRequest.failOnServerError(false))))
                .map(ReactorClientHttpResponse::new);
    }

}
