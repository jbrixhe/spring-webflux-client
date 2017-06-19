package com.webfluxclient.codec;


import org.springframework.http.HttpStatus;

public class HttpClientException extends HttpException{
    public HttpClientException(HttpStatus httpStatus) {
        super(httpStatus);
    }
    
    public HttpClientException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
