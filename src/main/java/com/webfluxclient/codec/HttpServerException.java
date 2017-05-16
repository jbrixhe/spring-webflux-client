package com.webfluxclient.codec;


import org.springframework.http.HttpStatus;

public class HttpServerException extends HttpException{
    public HttpServerException(HttpStatus httpStatus) {
        super(httpStatus);
    }
    
    public HttpServerException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
    
    public HttpServerException(HttpStatus httpStatus, String message, Throwable cause) {
        super(httpStatus, message, cause);
    }
    
    public HttpServerException(HttpStatus httpStatus, Throwable cause) {
        super(httpStatus, cause);
    }
    
    public HttpServerException(HttpStatus httpStatus, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(httpStatus, message, cause, enableSuppression, writableStackTrace);
    }
}
