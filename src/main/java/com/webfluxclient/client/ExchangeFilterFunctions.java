package com.webfluxclient.client;

import com.webfluxclient.RequestInterceptor;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunction.ofRequestProcessor;

class ExchangeFilterFunctions {

    static ExchangeFilterFunction requestInterceptorFilterFunction(RequestInterceptor requestInterceptor) {
        return ofRequestProcessor(clientRequest ->
                    Mono.just(clientRequest)
                        .map(requestInterceptor::process));
    }
}
