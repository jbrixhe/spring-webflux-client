package com.webfluxclient.client;

import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

import java.util.List;

import static java.util.Collections.unmodifiableList;

class ExtendedExchangeStrategies implements ExchangeStrategies {
    private List<HttpMessageReader<?>> httpMessageReaders;
    private List<HttpMessageWriter<?>> httpMessageWriters;

    private ExtendedExchangeStrategies(List<HttpMessageReader<?>> httpMessageReaders, List<HttpMessageWriter<?>> httpMessageWriters) {
        this.httpMessageReaders = unmodifiableList(httpMessageReaders);
        this.httpMessageWriters = unmodifiableList(httpMessageWriters);
    }

    public static ExchangeStrategies of(ClientCodecConfigurer codecConfigurer) {
        return new ExtendedExchangeStrategies(codecConfigurer.getReaders(), codecConfigurer.getWriters());
    }


    @Override
    public List<HttpMessageReader<?>> messageReaders() {
        return httpMessageReaders;
    }

    @Override
    public List<HttpMessageWriter<?>> messageWriters() {
        return httpMessageWriters;
    }
}
