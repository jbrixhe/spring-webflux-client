package com.webfluxclient;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Strategy for reading from a {@link ClientHttpResponse} and decoding
 * the stream of bytes to exceptions of type {@code <T>}.
 *
 * @author Jérémy Brixhe
 */
public interface HttpErrorReader {

    /**
     * Whether the given {@link HttpStatus} is supported by this reader.
     * @param httpStatus received by the client
     * @return {@code true} if supported, {@code false} otherwise
     */
    boolean canRead(HttpStatus httpStatus);

    /**
     * Read from the input message and return a error {@link Flux}
     *
     * @param inputMessage the message to read from
     * @return the error {@link Flux} with the decoded exception
     * */
    <T> Flux<T> read(ClientHttpResponse inputMessage);

    /**
     * Read from the input message and return a error {@link Mono}
     *
     * @param inputMessage the message to read from
     * @return the error {@link Mono} with the decoded exception
     * */
    <T> Mono<T> readMono(ClientHttpResponse inputMessage);

}
