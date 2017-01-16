package com.github.jbrixhe.reactiveclient.handler;

import com.github.jbrixhe.reactiveclient.request.RequestTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface InvocationHandlerFactory {

    InvocationHandler create(List<RequestTemplate> requestTemplates);

    class Default implements InvocationHandlerFactory {

        @Override
        public InvocationHandler create(List<RequestTemplate> requestTemplates) {
            Map<Method, MethodHandler> invocationDispatcher = requestTemplates
                    .stream()
                    .collect(Collectors.toMap(RequestTemplate::getTargetMethod, ReactorMethodHandler::new));

            return new ReactiveInvocationHandler(invocationDispatcher);
        }
    }
}
