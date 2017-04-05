package com.reactiveclient;

import org.springframework.http.HttpStatus;

import java.io.InputStream;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Strategy for decoding a {@link InputStream} into a {@link RuntimeException} of type {@code <T>}.
 *
 * @author Jérémy Brixhe
 * @param <T> the type of the exception supported by the decoder
 * */
public interface ErrorDecoder<T extends RuntimeException> {

    /**
     * Whether the decoder supports the given target status code of the response.
     *
     * @param httpStatus the status code received by the client
     * @return {@code true} if supported, {@code false} otherwise
     * */
    boolean canDecode(HttpStatus httpStatus);

    /**
     *
     * @param httpStatus the status code received by the client
     * @param inputStream the {@code InputStream} input stream to decode
     * @return the decoded exception
     * */
    T decode(HttpStatus httpStatus, InputStream inputStream);

    /**
     * Return a new {@code ErrorDecoder} described by the given predicate and bifunction functions.
     * All provided functions has to be initialized.
     *
     * @param statusPredicate the predicate function for accepted {@link HttpStatus}
     * @param errorDecoder the bifunction function to decode {@code InputStream}
     * @return the new {@code ExchangeStrategies}
     */
    static <T extends RuntimeException> ErrorDecoder<T> of(Predicate<HttpStatus> statusPredicate,
                                                           BiFunction<HttpStatus, InputStream, T> errorDecoder) {
        return new ErrorDecoder<T>() {
            @Override
            public boolean canDecode(HttpStatus httpStatus) {
                return statusPredicate.test(httpStatus);
            }

            @Override
            public T decode(HttpStatus httpStatus, InputStream inputStream) {
                return errorDecoder.apply(httpStatus, inputStream);
            }
        };
    }
}
