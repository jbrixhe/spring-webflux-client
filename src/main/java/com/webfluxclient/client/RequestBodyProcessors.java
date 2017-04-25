package com.webfluxclient.client;

import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import static com.webfluxclient.utils.ResolvableTypeUtils.isFormData;

public class RequestBodyProcessors {

    public static RequestBodyProcessor forType(ResolvableType bodyType) {
        if (bodyType == null) {
            return body -> BodyInserters.empty();
        } else if (Publisher.class.isAssignableFrom(bodyType.getRawClass())) {
            ResolvableType contentType = bodyType.getGeneric(0);
            if (DataBuffer.class.isAssignableFrom(contentType.getRawClass())) {
                return body -> BodyInserters.fromDataBuffers((Publisher<DataBuffer>) body);
            } else {
                return body -> BodyInserters.fromPublisher((Publisher<?>) body, contentType);
            }
        } else if (Resource.class.isAssignableFrom(bodyType.getRawClass())) {
            return body ->  BodyInserters.fromResource((Resource) body);
        } else if (isFormData(bodyType)) {
            return body ->  BodyInserters.fromFormData((MultiValueMap<String, String>) body);
        } else {
            return body ->  BodyInserters.fromObject(body);
        }
    }
}
