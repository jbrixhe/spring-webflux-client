package com.reactiveclient.example.client;

import com.reactiveclient.ErrorDecoder;
import org.springframework.http.HttpStatus;

import java.io.InputStream;

public class NotFoundErrorDecoder implements ErrorDecoder {

    @Override
    public boolean canDecode(HttpStatus httpStatus) {
        return HttpStatus.NOT_FOUND.equals(httpStatus);
    }

    @Override
    public RuntimeException decode(HttpStatus httpStatus, InputStream inputStream) {
        return new ResourceNotFoundException();
    }
}
