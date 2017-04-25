package com.webfluxclient.client;

import com.webfluxclient.utils.Types;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import static com.webfluxclient.utils.Types.*;

class RequestBodyProcessors {

    static RequestBodyProcessor<?> forType(ResolvableType bodyType) {
        if (bodyType == null) {
            return forEmpty();
        } else if (isDataBuffer(bodyType)) {
            return forDataBuffer();
        } else if (isPublisher(bodyType)) {
            return forPublisher(bodyType.getGeneric(0));
        } else if (Types.isResource(bodyType)) {
            return forResource();
        } else if (isFormData(bodyType)) {
            return forFormData();
        } else {
            return forObject();
        }
    }

    static RequestBodyProcessor<Publisher<DataBuffer>> forDataBuffer() {
        return BodyInserters::fromDataBuffers;
    }

    static <T> RequestBodyProcessor<Publisher<T>> forPublisher(ResolvableType contentType) {
        return body -> BodyInserters.fromPublisher(body, contentType);
    }

    static RequestBodyProcessor<Resource> forResource() {
        return BodyInserters::fromResource;
    }

    static RequestBodyProcessor<MultiValueMap<String, String>> forFormData() {
        return BodyInserters::fromFormData;
    }


    static RequestBodyProcessor<Object> forObject() {
        return BodyInserters::fromObject;
    }

    static RequestBodyProcessor<?> forEmpty() {
        return body -> BodyInserters.empty();
    }

}
