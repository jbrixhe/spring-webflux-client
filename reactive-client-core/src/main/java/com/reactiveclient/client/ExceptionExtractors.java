package com.reactiveclient.client;

import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

class ExceptionExtractors {

    public static <T> ExceptionExtractor<Mono<T>, ClientHttpResponse> toMono() {
        return (inputMessage, context) -> readWithMessageReaders(inputMessage.getStatusCode(),
                context,
                httpExceptionReader -> httpExceptionReader.readMono(inputMessage));
    }

    public static <T> ExceptionExtractor<Flux<T>, ClientHttpResponse> toFlux() {
        return (inputMessage, context) -> readWithMessageReaders(inputMessage.getStatusCode(),
                context,
                httpExceptionReader -> httpExceptionReader.read(inputMessage));
    }

    private static <T, S extends Publisher<T>> S readWithMessageReaders(
            HttpStatus httpStatus,
            ExceptionExtractor.Context context,
            Function<HttpExceptionReader, S> readerFunction) {

        HttpExceptionReader httpExceptionReader = context.exceptionReaders()
                .get()
                .filter(r -> r.canRead(httpStatus))
                .findFirst()
                .orElseThrow(() -> new RuntimeException());

        return readerFunction.apply(httpExceptionReader);
    }
}
