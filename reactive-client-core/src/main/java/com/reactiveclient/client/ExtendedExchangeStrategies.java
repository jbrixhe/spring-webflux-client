package com.reactiveclient.client;

import com.reactiveclient.HttpErrorReader;
import com.reactiveclient.client.codec.ExtendedClientCodecConfigurer;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

import java.util.function.Supplier;
import java.util.stream.Stream;

interface ExtendedExchangeStrategies extends ExchangeStrategies {

    Supplier<Stream<HttpErrorReader>> exceptionReader();

    static ExtendedExchangeStrategies of(Supplier<Stream<HttpMessageWriter<?>>> messageWriters,
                                 Supplier<Stream<HttpMessageReader<?>>> messageReaders,
                                 Supplier<Stream<HttpErrorReader>> errorReaders) {

        return new ExtendedExchangeStrategies() {
            @Override
            public Supplier<Stream<HttpErrorReader>> exceptionReader() {
                return checkForNull(errorReaders);
            }
            @Override
            public Supplier<Stream<HttpMessageReader<?>>> messageReaders() {
                return checkForNull(messageReaders);
            }
            @Override
            public Supplier<Stream<HttpMessageWriter<?>>> messageWriters() {
                return checkForNull(messageWriters);
            }
            private <T> Supplier<Stream<T>> checkForNull(Supplier<Stream<T>> supplier) {
                return supplier != null ? supplier : Stream::empty;
            }
        };
    }

    static ExtendedExchangeStrategies of(ExtendedClientCodecConfigurer configurer) {
        return of(configurer.getWriters()::stream,
                configurer.getReaders()::stream,
                configurer.getErrorReaders()::stream);
    }
}
