package com.github.jbrixhe.reactiveclient.request;

import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@AllArgsConstructor
public class RequestHandler implements InvocationHandler {

    private ReactiveContext reactiveContext;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
