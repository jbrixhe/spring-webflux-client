package com.reactiveclient.handler;

import com.reactiveclient.metadata.MethodMetadata;
import com.reactiveclient.metadata.request.ReactiveRequest;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class DefaultReactiveMethodHandler implements ReactiveMethodHandler {

    private WebClient client;
    private MethodMetadata methodMetadata;
    private Consumer<ReactiveRequest> requestInterceptor;
    private Function<ReactiveRequest, Publisher<?>> requestFunction;

    public DefaultReactiveMethodHandler(MethodMetadata methodMetadata, WebClient client, Consumer<ReactiveRequest> requestInterceptor) {
        this.client = client;
        this.methodMetadata = methodMetadata;
        this.requestInterceptor = requestInterceptor;
        this.requestFunction = buildWebClient(methodMetadata.getBodyType())
                .andThen(responseExtractor(methodMetadata.getResponseType()));
    }

    @Override
    public Object invoke(Object[] args) {
        ReactiveRequest reactiveRequest = methodMetadata.getReactiveRequestTemplate().apply(args);

        requestInterceptor.accept(reactiveRequest);

        return requestFunction.apply(reactiveRequest);
    }

    private Function<ReactiveRequest, WebClient.ResponseSpec> buildWebClient(ResolvableType bodyType) {
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
        } else if (Publisher.class.isAssignableFrom(bodyType.getRawClass())) { //
            return BodyInserters.fromPublisher(Publisher.class.cast(body), bodyType.getGeneric(0));
        } else {
            return BodyInserters.fromObject(body);
        }
    }
}
