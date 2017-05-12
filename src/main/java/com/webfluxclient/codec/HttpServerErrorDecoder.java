package com.webfluxclient.codec;

import com.webfluxclient.utils.DataBuffers;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;

public class HttpServerErrorDecoder implements ErrorDecoder<HttpServerException> {
    @Override
    public boolean canDecode(HttpStatus httpStatus) {
        return httpStatus.is5xxServerError();
    }

    @Override
    public HttpServerException decode(HttpStatus httpStatus, DataBuffer inputMessage) {
        return new HttpServerException(httpStatus, DataBuffers.readToString(inputMessage));
    }
}
