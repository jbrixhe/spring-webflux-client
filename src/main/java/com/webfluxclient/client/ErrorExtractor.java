package com.webfluxclient.client;

import com.webfluxclient.codec.HttpErrorReader;
import org.springframework.http.client.reactive.ClientHttpResponse;

import java.util.function.Supplier;
import java.util.stream.Stream;

@FunctionalInterface
interface ErrorExtractor<T, M extends ClientHttpResponse> {

    T extract(M inputMessage, Context context);

    interface Context {

        Supplier<Stream<HttpErrorReader>> exceptionReaders();

    }

}