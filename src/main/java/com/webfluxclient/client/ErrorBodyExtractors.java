package com.webfluxclient.client;

import com.webfluxclient.codec.HttpErrorReader;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.web.reactive.function.BodyExtractor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

class ErrorBodyExtractors {

    static <T> BodyExtractor<Mono<T>, ClientHttpResponse> toMono(List<HttpErrorReader> httpErrorReaders) {
        return (inputMessage, context) -> readWithMessageReaders(
                inputMessage.getStatusCode(),
                httpErrorReaders,
                httpExceptionReader -> httpExceptionReader.readMono(inputMessage));
    }

    static <T> BodyExtractor<Flux<T>, ClientHttpResponse> toFlux(List<HttpErrorReader> httpErrorReaders) {
        return (inputMessage, context) -> readWithMessageReaders(inputMessage.getStatusCode(),
                httpErrorReaders,
                httpExceptionReader -> httpExceptionReader.read(inputMessage));
    }

    private static <T, S extends Publisher<T>> S readWithMessageReaders(
            HttpStatus httpStatus,
            List<HttpErrorReader> httpErrorReaders,
            Function<HttpErrorReader, S> readerFunction) {

        HttpErrorReader httpErrorReader = httpErrorReaders
                .stream()
                .filter(r -> r.canRead(httpStatus))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No HttpErrorReader available for status: " + httpStatus.value()));

        return readerFunction.apply(httpErrorReader);
    }
}
