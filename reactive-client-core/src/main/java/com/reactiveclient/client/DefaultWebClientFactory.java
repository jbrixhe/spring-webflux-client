package com.reactiveclient.client;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultWebClientFactory implements WebClientFactory {
    @Override
    public WebClient create(List<com.reactiveclient.ErrorDecoder> errorDecoders) {
        List<HttpExceptionReader> httpExceptionReaders = errorDecoders.stream()
                .map(DecoderHttpExceptionReader::new)
                .collect(Collectors.toList());

        httpExceptionReaders.add(new DecoderHttpExceptionReader(ErrorDecoders.defaultErrorDecoder()));

        return WebClient
                .builder()
                .exchangeFunction(new ExtendedExchangeFunction(httpExceptionReaders))
                .build();
    }
}
