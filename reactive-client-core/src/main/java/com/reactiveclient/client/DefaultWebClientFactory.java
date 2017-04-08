package com.reactiveclient.client;

import com.reactiveclient.DecoderHttpErrorReader;
import com.reactiveclient.ErrorDecoder;
import com.reactiveclient.HttpErrorReader;
import com.reactiveclient.RequestInterceptor;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultWebClientFactory implements WebClientFactory {

    @Override
    public WebClient create(List<ErrorDecoder> errorDecoders, RequestInterceptor requestInterceptor) {
        errorDecoders.add(new HttpClientErrorDecoder());
        errorDecoders.add(new HttpServerErrorDecoder());
        List<HttpErrorReader> httpErrorReaders = errorDecoders.stream()
                .map(DecoderHttpErrorReader::new)
                .collect(Collectors.toList());

        return WebClient
                .builder()
                .exchangeFunction(new ExtendedExchangeFunction(httpErrorReaders))
                .build();
    }
}
