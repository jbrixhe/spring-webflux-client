package com.webfluxclient.codec;


import org.springframework.http.HttpStatus;

public class HttpClientException extends HttpException{
    public HttpClientException(HttpStatus httpStatus) {
        super(httpStatus);
    }
    
    public HttpClientException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
    
    public HttpClientException(HttpStatus httpStatus, String message, Throwable cause) {
        super(httpStatus, message, cause);
    }
    
    public HttpClientException(HttpStatus httpStatus, Throwable cause) {
        super(httpStatus, cause);
    }
    
    public HttpClientException(HttpStatus httpStatus, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(httpStatus, message, cause, enableSuppression, writableStackTrace);
    }
}
