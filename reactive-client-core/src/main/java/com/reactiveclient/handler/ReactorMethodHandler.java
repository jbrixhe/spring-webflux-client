package com.reactiveclient.handler;

import com.reactiveclient.metadata.MethodMetadata;
import com.reactiveclient.metadata.request.Request;
import org.reactivestreams.Publisher;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Function;

public class ReactorMethodHandler implements MethodHandler {

    private MethodMetadata methodMetadata;
    private Function<Mono<ClientResponse>, Publisher<?>> responseExtractor;
    private Function<Object, BodyInserter<?, ? super ClientHttpRequest>> bodyInserterFunction;

    public ReactorMethodHandler(MethodMetadata methodMetadata) {
        this.methodMetadata = methodMetadata;
        this.responseExtractor = responseExractor(methodMetadata.getReturnType());
        this.bodyInserterFunction = bodyInserter(methodMetadata.getBodyType());
    }

    @Override
    public Object invoke(Object[] args) {
        Request request = methodMetadata.getRequestTemplate().apply(args);
        WebClient client = WebClient.create(new ReactorClientHttpConnector());
        ClientRequest<?> clientRequest = buildClientRequest(request);

        return responseExtractor.apply(client.exchange(clientRequest));
    }

    private ClientRequest<?> buildClientRequest(Request request) {
        return ClientRequest.method(request.getHttpMethod(), request.getUri())
                .headers(request.getHttpHeaders())
                .body(bodyInserterFunction.apply(request.getBody()));
    }


    Function<Mono<ClientResponse>, Publisher<?>> responseExractor(Type returnType) {
        if (ParameterizedType.class.isInstance(returnType)) {
            ParameterizedType parameterizedType = (ParameterizedType) returnType;
            Type argumentType = parameterizedType.getActualTypeArguments()[0];
            if (ParameterizedType.class.isInstance(argumentType)) {
                throw new IllegalArgumentException("Embedded generic type not supported yet.");
            }

            if (Mono.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
                return clientResponseMono -> clientResponseMono.then(clientResponse -> clientResponse.bodyToMono((Class<?>) argumentType));
            } else if (Flux.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
                return clientResponseMono -> clientResponseMono.flatMap(clientResponse -> clientResponse.bodyToFlux((Class<?>) argumentType));
            }
        } else if (void.class.equals(returnType)) {
            return Mono::then;
        }

        throw new IllegalArgumentException();
    }

    Function<Object, BodyInserter<?, ? super ClientHttpRequest>> bodyInserter(Type bodyType) {
        if (bodyType == null) {
            return o -> BodyInserters.empty();
        } else if (ParameterizedType.class.isInstance(bodyType)) {
            ParameterizedType parameterizedType = (ParameterizedType) bodyType;
            Type argumentType = parameterizedType.getActualTypeArguments()[0];
            if (ParameterizedType.class.isInstance(argumentType)) {
                throw new IllegalArgumentException("Embedded generic type not supported yet.");
            }

            if (Publisher.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
                return o -> BodyInserters.fromPublisher((Publisher) o, (Class<?>) argumentType);
            }
        } else {
            return o -> BodyInserters.fromObject(o);
        }

        throw new IllegalArgumentException();
    }
}
