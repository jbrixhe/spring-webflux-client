package com.webfluxclient.codec;


import org.springframework.http.HttpStatus;

public class HttpServerException extends HttpException{
    public HttpServerException(HttpStatus httpStatus) {
        super(httpStatus);
    }
    
    public HttpServerException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
