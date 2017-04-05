package com.reactiveclient.client;

import com.reactiveclient.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.io.InputStream;

public class HttpClientErrorDecoder implements ErrorDecoder<HttpClientErrorException> {
    @Override
    public boolean canDecode(HttpStatus httpStatus) {
        return httpStatus.is4xxClientError();
    }

    @Override
    public HttpClientErrorException decode(HttpStatus httpStatus, InputStream inputStream) {
        return new HttpClientErrorException(httpStatus, DataBuffers.readToString(inputStream));
    }
}
