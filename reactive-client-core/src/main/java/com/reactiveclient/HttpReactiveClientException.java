package com.reactiveclient;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class HttpReactiveClientException extends RuntimeException {
    private HttpStatus httpStatus;

    public HttpReactiveClientException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpReactiveClientException(HttpStatus httpStatus, Throwable cause) {
        super(cause);
        this.httpStatus = httpStatus;
    }
}
