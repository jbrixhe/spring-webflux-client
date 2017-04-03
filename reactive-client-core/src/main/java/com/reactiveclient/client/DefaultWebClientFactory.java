package com.reactiveclient.client;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

import static com.reactiveclient.ErrorDecoders.stringErrorDecoder;

public class DefaultWebClientFactory implements WebClientFactory {
    @Override
    public WebClient create(List<com.reactiveclient.ErrorDecoder> errorDecoders) {
        errorDecoders.add(stringErrorDecoder(HttpStatus::is4xxClientError, HttpClientErrorException.class));
        errorDecoders.add(stringErrorDecoder(HttpStatus::is5xxServerError, HttpServerErrorException.class));
        List<HttpExceptionReader> httpExceptionReaders = errorDecoders.stream()
                .map(DecoderHttpExceptionReader::new)
                .collect(Collectors.toList());

        return WebClient
                .builder()
                .exchangeFunction(new ExtendedExchangeFunction(httpExceptionReaders))
                .build();
    }
}
