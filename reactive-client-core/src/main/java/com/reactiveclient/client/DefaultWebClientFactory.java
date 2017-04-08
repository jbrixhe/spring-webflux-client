package com.reactiveclient.client;

import com.reactiveclient.client.codec.ExtendedClientCodecConfigurer;
import org.springframework.web.reactive.function.client.WebClient;

public class DefaultWebClientFactory implements WebClientFactory {

    @Override
    public WebClient create(ExtendedClientCodecConfigurer codecs) {
        ExtendedExchangeStrategies extendedExchangeStrategies = ExtendedExchangeStrategies.of(codecs);

        return WebClient
                .builder()
                .exchangeFunction(new ExtendedExchangeFunction(extendedExchangeStrategies))
                .build();
    }
}
