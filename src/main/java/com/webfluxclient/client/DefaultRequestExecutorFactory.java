package com.webfluxclient.client;

import com.webfluxclient.codec.ExtendedClientCodecConfigurer;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunctions;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

public class DefaultRequestExecutorFactory implements RequestExecutorFactory {

    @Override
    public RequestExecutor build(ExtendedClientCodecConfigurer codecConfigurer, ExchangeFilterFunction exchangeFilterFunction) {
        ExchangeStrategies exchangeStrategies = ExtendedExchangeStrategies.of(codecConfigurer);
        WebClient webClient = WebClient
                .builder()
                .filters(exchangeFilterFunctions -> {
                    if (exchangeFilterFunction != null) {
                        exchangeFilterFunctions.add(exchangeFilterFunction);
                    }
                })
                .exchangeFunction(ExchangeFunctions.create(new ReactorClientHttpConnector(), exchangeStrategies))
                .build();

        return new DefaultRequestExecutor(webClient);
    }
}
