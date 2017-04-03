package com.reactiveclient.client;

import com.reactiveclient.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpResponse;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class DecoderHttpExceptionReader implements HttpExceptionReader {

    private ErrorDecoder errorDecoder;

    DecoderHttpExceptionReader(ErrorDecoder errorDecoder) {
        this.errorDecoder = errorDecoder;
    }

    @Override
    public boolean canRead(HttpStatus httpStatus) {
        return errorDecoder.canDecode(httpStatus);
    }

    @Override
    public <T> Flux<T> read(ClientHttpResponse inputMessage) {
        return decodeInternal(inputMessage);
    }

    @Override
    public <T> Mono<T> readMono(ClientHttpResponse inputMessage) {
        return (Mono<T>) decodeInternal(inputMessage).singleOrEmpty();
    }

    private <T> Flux<T> decodeInternal(ClientHttpResponse inputMessage) {
        return Flux.from(inputMessage.getBody())
                .map(dataBuffer -> {
                    throw errorDecoder.decode(inputMessage.getStatusCode(), dataBuffer.asInputStream());
                });
    }
}
