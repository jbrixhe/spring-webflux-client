package com.webfluxclient.client;

import com.webfluxclient.RequestInterceptor;
import com.webfluxclient.codec.ExtendedClientCodecConfigurer;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunction.ofRequestProcessor;

public class DefaultRequestExecutorFactory implements RequestExecutorFactory {

    @Override
    public RequestExecutor create(ExtendedClientCodecConfigurer codecs, RequestInterceptor requestInterceptor) {
        ExtendedExchangeStrategies extendedExchangeStrategies = ExtendedExchangeStrategies.of(codecs);
        WebClient webClient = WebClient
                .builder()
                .filter(ofRequestProcessor(clientRequest -> Mono.just(clientRequest).map(requestInterceptor::process)))
                .exchangeFunction(new ExtendedExchangeFunction(extendedExchangeStrategies))
                .build();
        return new DefaultRequestExecutor(webClient);
    }
}
