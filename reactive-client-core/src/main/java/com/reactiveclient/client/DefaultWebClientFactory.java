package com.reactiveclient.client;

import com.reactiveclient.DecoderHttpErrorReader;
import com.reactiveclient.ErrorDecoders;
import com.reactiveclient.HttpErrorReader;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultWebClientFactory implements WebClientFactory {
    @Override
    public WebClient create(List<com.reactiveclient.ErrorDecoder> errorDecoders) {
        errorDecoders.add(ErrorDecoders.stringAndStatusErrorDecoder(HttpStatus::is4xxClientError, HttpClientErrorException::new));
        errorDecoders.add(ErrorDecoders.stringAndStatusErrorDecoder(HttpStatus::is5xxServerError, HttpServerErrorException::new));
        List<HttpErrorReader> httpErrorReaders = errorDecoders.stream()
                .map(DecoderHttpErrorReader::new)
                .collect(Collectors.toList());

        return WebClient
                .builder()
                .exchangeFunction(new ExtendedExchangeFunction(httpErrorReaders))
                .build();
    }
}
