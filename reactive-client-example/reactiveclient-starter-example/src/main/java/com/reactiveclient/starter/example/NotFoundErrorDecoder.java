package com.reactiveclient.starter.example;

import com.reactiveclient.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class NotFoundErrorDecoder implements ErrorDecoder {
    @Override
    public boolean canDecode(HttpStatus httpStatus) {
        return httpStatus.is4xxClientError();
    }

    @Override
    public RuntimeException decode(HttpStatus httpStatus, InputStream inputStream) {
        return new ResourceNotFoundException();
    }
}
