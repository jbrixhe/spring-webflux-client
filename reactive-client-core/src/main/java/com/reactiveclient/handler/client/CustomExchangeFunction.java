package com.reactiveclient.handler.client;

import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.util.logging.Level;

public class CustomExchangeFunction implements ExchangeFunction {
    private final ClientHttpConnector connector;

    private final ExtendedExchangeStrategies strategies;

    private CustomExchangeFunction(
            ClientHttpConnector connector,
            ExtendedExchangeStrategies strategies) {
        this.connector = connector;
        this.strategies = strategies;
    }

    public static ExchangeFunction build() {
        return new CustomExchangeFunction(new NeverFailOnExceptionReactorClientHttpConnector(), ExtendedExchangeStrategiesImpl.build());
    }

    @Override
    public Mono<ClientResponse> exchange(ClientRequest request) {
        Assert.notNull(request, "'request' must not be null");

        return this.connector
                .connect(request.method(), request.url(),
                        clientHttpRequest -> request.writeTo(clientHttpRequest, this.strategies))
                .log("org.springframework.web.reactive.function.client", Level.FINE)
                .map(clientHttpResponse -> new CustomClientResponse(clientHttpResponse, this.strategies));
    }
}
