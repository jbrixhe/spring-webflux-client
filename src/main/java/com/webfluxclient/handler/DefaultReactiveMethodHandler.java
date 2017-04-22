package com.webfluxclient.handler;

import com.webfluxclient.RequestInterceptor;
import com.webfluxclient.metadata.MethodMetadata;
import com.webfluxclient.metadata.request.Request;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class DefaultReactiveMethodHandler implements ReactiveMethodHandler {

    private WebClient client;
    private MethodMetadata methodMetadata;
    private Function<Request, Publisher<?>> requestFunction;
    private RequestInterceptor requestInterceptor;

    public DefaultReactiveMethodHandler(MethodMetadata methodMetadata, WebClient client, RequestInterceptor requestInterceptor) {
        this.client = client;
        this.methodMetadata = methodMetadata;
        this.requestInterceptor = requestInterceptor;
        this.requestFunction = buildWebClient(methodMetadata.getBodyType())
                .andThen(responseExtractor(methodMetadata.getResponseType()));
    }

    DefaultReactiveMethodHandler() {
    }

    @Override
    public Object invoke(Object[] args) {
        Request request = methodMetadata.getRequestTemplate().apply(args);

        requestInterceptor.accept(request);

        return requestFunction.apply(request);
    }

    private Function<Request, WebClient.ResponseSpec> buildWebClient(ResolvableType bodyType) {
        return request -> client.method(request.getHttpMethod())
                .uri(request.expand())
                .headers(request.getHttpHeaders())
                .body(toBodyInserter(bodyType, request.getBody()))
                .retrieve();
    }

    private Function<WebClient.ResponseSpec, Publisher<?>> responseExtractor(ResolvableType returnType) {
        if (Mono.class.isAssignableFrom(returnType.getRawClass())) {
            return responseSpec -> responseSpec.bodyToMono(returnType.getGeneric(0).getRawClass());
        } else if (Flux.class.isAssignableFrom(returnType.getRawClass())) {
            return responseSpec -> responseSpec.bodyToFlux(returnType.getGeneric(0).getRawClass());
        } else if(void.class.isAssignableFrom(returnType.getRawClass())) {
            return responseSpec -> null;
        }
        throw new IllegalArgumentException();
    }

    private BodyInserter<?, ? super ClientHttpRequest> toBodyInserter(ResolvableType bodyType, Object body) {
        if (bodyType == null) {
            return BodyInserters.empty();
        } else if (Publisher.class.isAssignableFrom(bodyType.getRawClass())) {
            ResolvableType contentType = bodyType.getGeneric(0);
            if (DataBuffer.class.isAssignableFrom(contentType.getRawClass())) {
                return BodyInserters.fromDataBuffers((Publisher<DataBuffer>) body);
            } else {
                return BodyInserters.fromPublisher((Publisher<?>) body, contentType);
            }
        } else if (Resource.class.isAssignableFrom(bodyType.getRawClass())) {
            return BodyInserters.fromResource((Resource) body);
        } else if (isFormData(bodyType)) {
            return BodyInserters.fromFormData((MultiValueMap<String, String>) body);
        } else {
            return BodyInserters.fromObject(body);
        }
    }

    boolean isFormData(ResolvableType bodyType) {
        if (MultiValueMap.class.isAssignableFrom(bodyType.getRawClass())) {
            ResolvableType keyType = bodyType.getGeneric(0);
            ResolvableType valueType = bodyType.getGeneric(1);
            if (String.class.isAssignableFrom(keyType.getRawClass()) && String.class.isAssignableFrom(valueType.getRawClass())) {
                return true;
            }
        }

        return false;
    }
}
