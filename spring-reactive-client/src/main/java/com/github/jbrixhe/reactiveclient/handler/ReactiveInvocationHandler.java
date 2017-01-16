package com.github.jbrixhe.reactiveclient.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class ReactiveInvocationHandler implements InvocationHandler {

    private Map<Method, MethodHandler> invocationDispatcher;

    public ReactiveInvocationHandler(Map<Method, MethodHandler> invocationDispatcher) {
        this.invocationDispatcher = invocationDispatcher;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
