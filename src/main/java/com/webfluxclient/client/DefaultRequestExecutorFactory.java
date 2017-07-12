package com.webfluxclient.client;

import com.webfluxclient.RequestProcessor;
import com.webfluxclient.ResponseProcessor;
import com.webfluxclient.codec.ExtendedClientCodecConfigurer;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFunctions;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static com.webfluxclient.client.ExchangeFilterFunctions.requestProcessorFilter;
import static com.webfluxclient.client.ExchangeFilterFunctions.responseInterceptorFilter;

public class DefaultRequestExecutorFactory implements RequestExecutorFactory {

    @Override
    public RequestExecutor build(ExtendedClientCodecConfigurer codecConfigurer, List<RequestProcessor> requestProcessors, List<ResponseProcessor> responseProcessors) {
        ExchangeStrategies exchangeStrategies = ExtendedExchangeStrategies.of(codecConfigurer);

        RequestProcessor requestProcessor = requestProcessors.stream()
                .reduce(RequestProcessor::andThen)
                .orElseGet(() -> clientRequest -> clientRequest);

        ResponseProcessor responseProcessor = responseProcessors.stream()
                .reduce(ResponseProcessor::andThen)
                .orElseGet(() -> clientResponse -> clientResponse);

        WebClient webClient = WebClient
                .builder()
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(requestProcessorFilter(requestProcessor));
                    exchangeFilterFunctions.add(responseInterceptorFilter(responseProcessor));
                })
                .exchangeFunction(ExchangeFunctions.create(new ReactorClientHttpConnector(), exchangeStrategies))
                .build();

        return new DefaultRequestExecutor(webClient);
    }
}
