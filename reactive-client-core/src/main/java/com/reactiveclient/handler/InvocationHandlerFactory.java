package com.reactiveclient.handler;

import com.reactiveclient.metadata.MethodMetadata;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface InvocationHandlerFactory {

    InvocationHandler create(List<MethodMetadata> requestTemplates);

    class Default implements InvocationHandlerFactory {

        @Override
        public InvocationHandler create(List<MethodMetadata> requestTemplates) {
            Map<Method, MethodHandler> invocationDispatcher = requestTemplates
                    .stream()
                    .collect(Collectors.toMap(MethodMetadata::getTargetMethod, ReactiveMethodHandler::new));

            return new ReactiveInvocationHandler(invocationDispatcher);
        }
    }
}
