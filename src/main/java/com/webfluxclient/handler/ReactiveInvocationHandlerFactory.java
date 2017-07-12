package com.webfluxclient.handler;

import com.webfluxclient.RequestProcessor;
import com.webfluxclient.ResponseInterceptor;
import com.webfluxclient.codec.ExtendedClientCodecConfigurer;

import java.lang.reflect.InvocationHandler;
import java.net.URI;
import java.util.List;

public interface ReactiveInvocationHandlerFactory {
    InvocationHandler build(
            ExtendedClientCodecConfigurer codecConfigurer,
            List<RequestProcessor> requestProcessors,
            List<ResponseInterceptor> responseInterceptors,
            Class<?> target,
            URI uri);
}
