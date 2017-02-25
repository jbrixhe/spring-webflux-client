package com.reactiveclient;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ReactiveClientException extends RuntimeException {
    private HttpStatus httpStatus;

    public ReactiveClientException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public ReactiveClientException(HttpStatus httpStatus, Throwable cause) {
        super(cause);
        this.httpStatus = httpStatus;
    }
}
