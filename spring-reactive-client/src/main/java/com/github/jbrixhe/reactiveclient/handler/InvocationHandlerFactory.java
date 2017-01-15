package com.github.jbrixhe.reactiveclient.handler;

import java.lang.reflect.InvocationHandler;

public interface InvocationHandlerFactory {

    InvocationHandler newInstance();
}
