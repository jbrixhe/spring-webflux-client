package com.webfluxclient.client;

import com.webfluxclient.LogLevel;
import com.webfluxclient.Logger;
import com.webfluxclient.RequestProcessor;
import com.webfluxclient.ResponseProcessor;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
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
        return ofRequestProcessor(clientRequest -> Mono.just(requestProcessor.process(clientRequest)));
    }

    static ExchangeFilterFunction responseInterceptorFilter(ResponseProcessor responseProcessor) {
        return ofResponseProcessor(clientResponse -> Mono.just(responseProcessor.process(clientResponse)));
    }

    static ExchangeFilterFunction loggingFilter(Logger logger, LogLevel logLevel) {
        Assert.notNull(logger, "'logger' Can't be null");
        Assert.notNull(logLevel, "'logLevel' Can't be null");

        return (clientRequest, exchangeFunction) -> logClientRequest(
                clientRequest,
                exchangeFunction,
                logger,
                logLevel);
    }

    private static Mono<ClientResponse> logClientRequest(ClientRequest clientRequest, ExchangeFunction exchangeFunction, Logger logger, LogLevel logLevel) {
        boolean logHeaders = LogLevel.HEADERS.equals(logLevel);
        boolean logBasic = logHeaders || LogLevel.BASIC.equals(logLevel);

        long startNs = logBasic? System.nanoTime() : 0;
        if (logBasic) {
            logger.log(()-> "--> " + clientRequest.method() + ' ' + clientRequest.url());
            logger.log(()-> "Content-Type: " + clientRequest.headers().getContentType());
            if (logHeaders) {
                logger.log("Headers:");
                clientRequest.headers().forEach((name, values) -> {
                    if (!HttpHeaders.CONTENT_TYPE.equals(name))
                        logger.log(()-> " * " + name + ": " + values);
                });
            }
            logger.log(() -> "--> END " + clientRequest.method());
        }
        return exchangeFunction
                .exchange(clientRequest)
                .doOnEach(clientResponseSignal -> {
                    ClientResponse response;
                    if (logBasic && (response = clientResponseSignal.get()) != null) {
                        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
                        logger.log("<-- " + response.statusCode() + ' ' + clientRequest.method() + ' ' + clientRequest.url() + " (" + tookMs + "ms)");
                        if (logHeaders) {
                            logger.log("Headers:");
                            response.headers().asHttpHeaders().forEach((name, values) -> logger.log(()-> " - " + name + ": " + values));
                        }
                        logger.log(() -> "<-- END HTTP " + clientRequest.method());
                    }
                });
    }
}
