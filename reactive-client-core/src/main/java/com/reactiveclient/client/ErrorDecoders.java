package com.reactiveclient.client;

import com.reactiveclient.ErrorDecoder;
import com.reactiveclient.HttpReactiveClientException;

class ErrorDecoders {

    static ErrorDecoder defaultErrorDecoder(){
        return new StringErrorDecoder(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(), HttpReactiveClientException::new);
    }
}