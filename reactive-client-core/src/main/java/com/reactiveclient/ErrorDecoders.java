package com.reactiveclient;

import org.springframework.http.HttpStatus;
import org.springframework.util.ClassUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ErrorDecoders {

    public static <T extends RuntimeException> ErrorDecoder<T> stringErrorDecoder(Predicate<HttpStatus> httpStatusPredicate, Class<T> exceptionClass){
        return ErrorDecoder.of(httpStatusPredicate,
                (httpStatus, inputStream) -> createException(exceptionClass, httpStatus, readResponseBodyAsString(inputStream)));
    }

    private static <T extends RuntimeException> T createException(Class<T> exceptionClass, HttpStatus httpStatus, String errorMessage){
        if (ClassUtils.hasConstructor(exceptionClass, HttpStatus.class, String.class)) {
            Constructor<T> constructorIfAvailable = ClassUtils.getConstructorIfAvailable(exceptionClass, HttpStatus.class, String.class);
            return silentInvocation(constructorIfAvailable, httpStatus, errorMessage);
        } else if (ClassUtils.hasConstructor(exceptionClass, String.class, HttpStatus.class)) {
            Constructor<T> constructorIfAvailable = ClassUtils.getConstructorIfAvailable(exceptionClass, String.class, HttpStatus.class);
            return silentInvocation(constructorIfAvailable, httpStatus, errorMessage);
        } else if (ClassUtils.hasConstructor(exceptionClass, String.class)) {
            Constructor<T> constructorIfAvailable = ClassUtils.getConstructorIfAvailable(exceptionClass, String.class);
            return silentInvocation(constructorIfAvailable, errorMessage);
        }
        throw new RuntimeException("No constructor found for class "+ exceptionClass.getSimpleName());
    }

    private static <T> T silentInvocation(Constructor<T> constructorIfAvailable, Object... args) {
        try {
            return constructorIfAvailable.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Imnpossible to instanciate exception", e);
        }
    }

    private static String readResponseBodyAsString(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}