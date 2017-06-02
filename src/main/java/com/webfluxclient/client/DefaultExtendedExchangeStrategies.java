package com.webfluxclient.client;

import com.webfluxclient.codec.HttpErrorReader;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;

import java.util.List;

import static java.util.Collections.unmodifiableList;

class DefaultExtendedExchangeStrategies implements ExtendedExchangeStrategies {

    private List<HttpMessageWriter<?>> httpMessageWriters;
    private List<HttpMessageReader<?>> httpMessageReaders;
    private List<HttpErrorReader> httpExceptionReaders;

    DefaultExtendedExchangeStrategies(List<HttpMessageWriter<?>> httpMessageWriters, List<HttpMessageReader<?>> httpMessageReaders, List<HttpErrorReader> httpExceptionReaders) {
        this.httpMessageWriters = unmodifiableList(httpMessageWriters);
        this.httpMessageReaders = unmodifiableList(httpMessageReaders);
        this.httpExceptionReaders = unmodifiableList(httpExceptionReaders);
    }

    @Override
    public List<HttpErrorReader> exceptionReader() {
        return httpExceptionReaders;
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
