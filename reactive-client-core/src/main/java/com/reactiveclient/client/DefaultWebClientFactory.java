package com.reactiveclient.client;

import com.reactiveclient.DecoderHttpErrorReader;
import com.reactiveclient.HttpErrorReader;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultWebClientFactory implements WebClientFactory {
    @Override
    public WebClient create(List<com.reactiveclient.ErrorDecoder> errorDecoders) {
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
