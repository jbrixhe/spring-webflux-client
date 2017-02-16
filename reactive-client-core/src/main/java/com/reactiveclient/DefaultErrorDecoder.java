package com.reactiveclient;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class DefaultErrorDecoder implements ErrorDecoder {

    @Override
    public boolean canDecode(HttpStatus httpStatus) {
        return true;
    }

    @Override
    public RuntimeException decode(HttpStatus httpStatus, InputStream inputStream) {
        try {
            String exceptionMessage = IOUtils.toString(inputStream, Charset.forName("UTF-8"));
            return new ReactiveClientException(httpStatus, exceptionMessage);
        } catch (IOException e) {
            return new ReactiveClientException(httpStatus, e);
        }
    }

}
