package com.reactiveclient.handler.client;

import org.springframework.http.client.reactive.ClientHttpResponse;

import java.util.function.Supplier;
import java.util.stream.Stream;

@FunctionalInterface
public interface ExceptionExtractor<T, M extends ClientHttpResponse> {

    T extract(M inputMessage, Context context);

    interface Context {

        Supplier<Stream<HttpExceptionReader>> exceptionReaders();

    }

}