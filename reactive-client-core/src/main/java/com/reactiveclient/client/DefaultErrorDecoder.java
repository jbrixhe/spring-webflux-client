package com.reactiveclient.client;

import com.reactiveclient.ErrorDecoder;
import com.reactiveclient.ReactiveClientException;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

class DefaultErrorDecoder implements ErrorDecoder {

    @Override
    public boolean canDecode(HttpStatus httpStatus) {
        return httpStatus.is4xxClientError() || httpStatus.is5xxServerError();
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
