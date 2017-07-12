package com.webfluxclient.client;

import com.webfluxclient.RequestInterceptor;
import com.webfluxclient.ResponseInterceptor;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunction.ofRequestProcessor;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunction.ofResponseProcessor;

class ExchangeFilterFunctions {

    static ExchangeFilterFunction requestInterceptorFilter(RequestInterceptor requestInterceptor) {
        return ofRequestProcessor(clientRequest ->
                    Mono.just(clientRequest)
                        .map(requestInterceptor::process));
    }

    static ExchangeFilterFunction responseInterceptorFilter(ResponseInterceptor responseInterceptor) {
        return ofResponseProcessor(clientResponse ->
                    Mono.just(clientResponse)
                        .map(responseInterceptor::process));
    }
}
