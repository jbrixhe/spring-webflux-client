package com.webfluxclient.client;

import com.webfluxclient.codec.ExtendedClientCodecConfigurer;
import org.springframework.web.reactive.function.client.WebClient;

public class DefaultRequestExecutorFactory implements RequestExecutorFactory {

    @Override
    public RequestExecutor create(ExtendedClientCodecConfigurer codecs) {
        ExtendedExchangeStrategies extendedExchangeStrategies = ExtendedExchangeStrategies.of(codecs);
        WebClient webClient = WebClient
                .builder()
                .exchangeFunction(new ExtendedExchangeFunction(extendedExchangeStrategies))
                .build();
        return new DefaultRequestExecutor(webClient);
    }
}
