package com.reactiveclient.example.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactiveclient.ErrorDecoder;
import org.springframework.http.HttpStatus;

import java.io.InputStream;

public class NotFoundErrorDecoder implements ErrorDecoder {

    private ObjectMapper objectMapper;

    public NotFoundErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean canDecode(HttpStatus httpStatus) {
        return HttpStatus.BAD_REQUEST.equals(httpStatus);
    }

    @Override
    public RuntimeException decode(HttpStatus httpStatus, InputStream inputStream) {
        return null;
    }
}
