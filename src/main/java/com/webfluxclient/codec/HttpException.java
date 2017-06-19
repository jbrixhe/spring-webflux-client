package com.webfluxclient.codec;

import org.springframework.http.HttpStatus;

public abstract class HttpException extends RuntimeException {
    private HttpStatus httpStatus;
    
    public HttpException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
    
    public HttpException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }
    
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
