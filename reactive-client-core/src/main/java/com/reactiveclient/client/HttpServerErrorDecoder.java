package com.reactiveclient.client;

import com.reactiveclient.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.io.InputStream;

public class HttpServerErrorDecoder implements ErrorDecoder<HttpServerErrorException> {
    @Override
    public boolean canDecode(HttpStatus httpStatus) {
        return httpStatus.is5xxServerError();
    }

    @Override
    public HttpServerErrorException decode(HttpStatus httpStatus, InputStream inputStream) {
        return new HttpServerErrorException(httpStatus, DataBuffers.readToString(inputStream));
    }
}
