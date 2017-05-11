package com.webfluxclient.handler;

import com.webfluxclient.RequestInterceptor;
import com.webfluxclient.codec.ExtendedClientCodecConfigurer;

import java.lang.reflect.InvocationHandler;
import java.net.URI;

public interface ReactiveInvocationHandlerFactory {

    InvocationHandler build(ExtendedClientCodecConfigurer codecConfigurer, RequestInterceptor requestInterceptor, Class<?> target, URI uri);

}
