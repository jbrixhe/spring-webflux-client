package com.webfluxclient.handler;

import org.springframework.util.Assert;

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
        ClientMethodHandler clientMethodHandler = invocationDispatcher.get(method);
        
        Assert.notNull(clientMethodHandler, () -> "Couldn't find a MethodHandler for the method " + method);
        
        return clientMethodHandler.invoke(args);
    }
}
