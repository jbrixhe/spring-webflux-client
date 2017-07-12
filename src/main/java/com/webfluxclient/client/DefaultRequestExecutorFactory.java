package com.webfluxclient.client;

import com.webfluxclient.RequestInterceptor;
import com.webfluxclient.ResponseInterceptor;
import com.webfluxclient.codec.ExtendedClientCodecConfigurer;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFunctions;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static com.webfluxclient.client.ExchangeFilterFunctions.requestInterceptorFilter;
import static com.webfluxclient.client.ExchangeFilterFunctions.responseInterceptorFilter;

public class DefaultRequestExecutorFactory implements RequestExecutorFactory {

    @Override
    public RequestExecutor build(ExtendedClientCodecConfigurer codecConfigurer, List<RequestInterceptor> requestInterceptors, List<ResponseInterceptor> responseInterceptors) {
        ExchangeStrategies exchangeStrategies = ExtendedExchangeStrategies.of(codecConfigurer);

        RequestInterceptor requestInterceptor = requestInterceptors.stream()
                .reduce(RequestInterceptor::andThen)
                .orElseGet(() -> clientRequest -> clientRequest);

        ResponseInterceptor responseInterceptor = responseInterceptors.stream()
                .reduce(ResponseInterceptor::andThen)
                .orElseGet(() -> clientResponse -> clientResponse);

        WebClient webClient = WebClient
                .builder()
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(requestInterceptorFilter(requestInterceptor));
                    exchangeFilterFunctions.add(responseInterceptorFilter(responseInterceptor));
                })
                .exchangeFunction(ExchangeFunctions.create(new ReactorClientHttpConnector(), exchangeStrategies))
                .build();

        return new DefaultRequestExecutor(webClient);
    }
}
