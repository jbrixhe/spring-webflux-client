package com.webfluxclient.client;

import com.webfluxclient.LogLevel;
import com.webfluxclient.Logger;
import com.webfluxclient.RequestProcessor;
import com.webfluxclient.ResponseProcessor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

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

    static ExchangeFilterFunction loggingFilter(Logger logger, LogLevel logLevel) {
        return (clientRequest, exchangeFunction) -> log(
                clientRequest,
                exchangeFunction,
                logger,
                logLevel);
    }

    private static Mono<ClientResponse> log(ClientRequest clientRequest, ExchangeFunction exchangeFunction,  Logger logger, LogLevel logLevel) {
        if (LogLevel.NONE.equals(logLevel)) {
            return exchangeFunction.exchange(clientRequest);
        }

        boolean logHeaders = LogLevel.HEADERS.equals(logLevel);
        long startNs = System.nanoTime();
        return Mono.just(clientRequest)
                .log()
                .doOnEach(clientRequestSignal -> {
                    ClientRequest request = clientRequestSignal.get();
                    logger.log(()-> "--> " + request.method() + ' ' + request.url());
                    logger.log(()-> "Content-Type: " + request.headers().getContentType());
                    if (logHeaders) {
                        logger.log("Headers:");
                        request.headers().forEach((name, values) -> {
                            if (!HttpHeaders.CONTENT_TYPE.equals(name))
                                logger.log(()-> " * " + name + ": " + values);
                        });
                    }
                    logger.log(() -> "--> END " + clientRequest.method());
                })
                .flatMap(exchangeFunction::exchange)
                .doOnEach(clientResponseSignal -> {
                    ClientResponse response = clientResponseSignal.get();
                    long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
                    logger.log("<-- " + response.statusCode() + ' ' + clientRequest.method() + ' ' + clientRequest.url() + " (" + tookMs + "ms");
                    if (logHeaders) {
                        logger.log("Headers:");
                        response.headers().asHttpHeaders().forEach((name, values) -> logger.log(()-> " * " + name + ": " + values));
                    }
                    logger.log(() -> "<-- END HTTP " + clientRequest.method());
                });
    }
}
