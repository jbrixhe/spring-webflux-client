package com.webfluxclient.codec;

import com.webfluxclient.utils.DataBuffers;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

class HttpClientErrorDecoder implements ErrorDecoder<HttpClientErrorException> {
    @Override
    public boolean canDecode(HttpStatus httpStatus) {
        return httpStatus.is4xxClientError();
    }

    @Override
    public HttpClientErrorException decode(HttpStatus httpStatus, DataBuffer inputMessage) {
        return new HttpClientErrorException(httpStatus, DataBuffers.readToString(inputMessage));
    }
}
