package com.webfluxclient.codec;

import com.webfluxclient.utils.DataBuffers;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;

public class HttpClientErrorDecoder implements ErrorDecoder<HttpClientException> {
    @Override
    public boolean canDecode(HttpStatus httpStatus) {
        return httpStatus.is4xxClientError();
    }

    @Override
    public HttpClientException decode(HttpStatus httpStatus, DataBuffer inputMessage) {
        return new HttpClientException(httpStatus, DataBuffers.readToString(inputMessage));
    }
}
