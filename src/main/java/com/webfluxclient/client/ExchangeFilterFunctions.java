package com.webfluxclient.client;

import com.webfluxclient.RequestProcessor;
import com.webfluxclient.ResponseProcessor;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunction.ofRequestProcessor;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunction.ofResponseProcessor;

class ExchangeFilterFunctions {

    static ExchangeFilterFunction requestProcessorFilter(RequestProcessor requestProcessor) {
        return ofRequestProcessor(clientRequest ->
                    Mono.just(clientRequest)
                        .map(requestProcessor::process));
    }

    static ExchangeFilterFunction responseInterceptorFilter(ResponseProcessor responseProcessor) {
        return ofResponseProcessor(clientResponse ->
                    Mono.just(clientResponse)
                        .map(responseProcessor::process));
    }
}
