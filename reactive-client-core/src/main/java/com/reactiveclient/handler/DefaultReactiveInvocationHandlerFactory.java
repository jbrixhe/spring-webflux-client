package com.reactiveclient.handler;

import com.reactiveclient.metadata.MethodMetadata;
import com.reactiveclient.metadata.request.Request;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DefaultReactiveInvocationHandlerFactory  implements ReactiveInvocationHandlerFactory {

    @Override
    public InvocationHandler create(List<MethodMetadata> requestTemplates, WebClient webClient, Consumer<Request> requestConsumer) {
        Map<Method, ReactiveMethodHandler> invocationDispatcher = requestTemplates
                .stream()
                .collect(Collectors.toMap(MethodMetadata::getTargetMethod, methodMetadata -> new DefaultReactiveMethodHandler(methodMetadata, webClient, requestConsumer)));

        return new DefaultReactiveInvocationHandler(invocationDispatcher);
    }
}
