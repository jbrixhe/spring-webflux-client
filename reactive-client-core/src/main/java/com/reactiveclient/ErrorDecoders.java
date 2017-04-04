package com.reactiveclient;

import org.springframework.http.HttpStatus;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ErrorDecoders {

    public static <T extends RuntimeException> ErrorDecoder<T> stringErrorDecoder(Predicate<HttpStatus> httpStatusPredicate, Function<String, T> errorDecoder){
        return ErrorDecoder.of(httpStatusPredicate, (httpStatus, inputStream) -> errorDecoder.apply(readResponseBodyAsString(inputStream)));
    }

    public static <T extends RuntimeException> ErrorDecoder<T> stringAndStatusErrorDecoder(Predicate<HttpStatus> httpStatusPredicate, BiFunction<HttpStatus, String, T> errorDecoder){
        return ErrorDecoder.of(httpStatusPredicate, (httpStatus, inputStream) -> errorDecoder.apply(httpStatus, readResponseBodyAsString(inputStream)));
    }

    private static String readResponseBodyAsString(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}