package com.reactiveclient;

import org.springframework.http.HttpStatus;

import java.io.InputStream;

public interface ErrorDecoder {

    boolean canDecode(HttpStatus httpStatus);

    RuntimeException decode(HttpStatus httpStatus, InputStream inputStream);
}
