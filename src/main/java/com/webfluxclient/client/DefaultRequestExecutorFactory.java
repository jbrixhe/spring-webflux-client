package com.webfluxclient.client;

import com.webfluxclient.RequestInterceptor;
import com.webfluxclient.codec.ExtendedClientCodecConfigurer;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.webfluxclient.client.ExchangeFilterFunctions.requestInterceptorFilterFunction;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunction.ofRequestProcessor;

public class DefaultRequestExecutorFactory implements RequestExecutorFactory {

    @Override
    public RequestExecutor build(ExtendedClientCodecConfigurer codecs, List<RequestInterceptor> requestInterceptors) {
        ExtendedExchangeStrategies extendedExchangeStrategies = ExtendedExchangeStrategies.of(codecs);

        WebClient webClient = WebClient
                .builder()
                .filter(requestInterceptorFilterFunction(requestInterceptors))
                .exchangeFunction(new ExtendedExchangeFunction(extendedExchangeStrategies))
                .build();

        return new DefaultRequestExecutor(webClient);
    }
}
