package com.webfluxclient.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class DefaultReactiveInvocationHandler implements InvocationHandler {

    private Map<Method, ClientMethodHandler> invocationDispatcher;

    public DefaultReactiveInvocationHandler(Map<Method, ClientMethodHandler> invocationDispatcher) {
        this.invocationDispatcher = invocationDispatcher;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return invocationDispatcher.get(method).invoke(args);
    }
}
