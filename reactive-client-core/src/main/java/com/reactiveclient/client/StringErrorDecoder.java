package com.reactiveclient.client;

import com.reactiveclient.ErrorDecoder;
import com.reactiveclient.HttpReactiveClientException;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class StringErrorDecoder implements ErrorDecoder {

    private BiFunction<HttpStatus, String, RuntimeException> exceptionTransformer;
    private Predicate<HttpStatus> statusPredicate;

    public StringErrorDecoder(Predicate<HttpStatus> statusPredicate, BiFunction<HttpStatus, String, RuntimeException> exceptionTransformer) {
        this.exceptionTransformer = exceptionTransformer;
        this.statusPredicate = statusPredicate;
    }

    @Override
    public boolean canDecode(HttpStatus httpStatus) {
        return statusPredicate.test(httpStatus);
    }

    @Override
    public RuntimeException decode(HttpStatus httpStatus, InputStream inputStream) {
        try {
            String exceptionMessage = IOUtils.toString(inputStream, Charset.forName("UTF-8"));
            return exceptionTransformer.apply(httpStatus, exceptionMessage);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
