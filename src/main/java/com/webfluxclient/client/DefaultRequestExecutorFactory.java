package com.webfluxclient.client;

import com.webfluxclient.RequestInterceptor;
import com.webfluxclient.codec.ExtendedClientCodecConfigurer;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFunctions;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static com.webfluxclient.client.ExchangeFilterFunctions.requestInterceptorFilterFunction;

public class DefaultRequestExecutorFactory implements RequestExecutorFactory {

    @Override
    public RequestExecutor build(ExtendedClientCodecConfigurer codecConfigurer, List<RequestInterceptor> requestInterceptors) {
        ExchangeStrategies exchangeStrategies = ExtendedExchangeStrategies.of(codecConfigurer);
        RequestInterceptor requestInterceptor = requestInterceptors.stream()
                .reduce(RequestInterceptor::andThen)
                .orElseGet(() -> reactiveRequest -> reactiveRequest);

        WebClient webClient = WebClient
                .builder()
                .filter(requestInterceptorFilterFunction(requestInterceptor))
                .exchangeFunction(ExchangeFunctions.create(new ReactorClientHttpConnector(), exchangeStrategies))
                .build();

        return new DefaultRequestExecutor(webClient);
    }
}
