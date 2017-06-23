package com.webfluxclient.client;

import com.webfluxclient.RequestInterceptor;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunction.ofRequestProcessor;

class ExchangeFilterFunctions {

    static ExchangeFilterFunction requestInterceptorFilterFunction(List<RequestInterceptor> requestInterceptors) {
        RequestInterceptor requestInterceptor = requestInterceptors.stream()
                .reduce(RequestInterceptor::andThen)
                .orElseGet(() -> reactiveRequest -> reactiveRequest);

        return ofRequestProcessor(clientRequest ->
                    Mono.just(clientRequest)
                        .map(requestInterceptor::process));
    }
}
