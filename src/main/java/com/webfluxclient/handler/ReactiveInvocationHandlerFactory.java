package com.webfluxclient.handler;

import com.webfluxclient.RequestProcessor;
import com.webfluxclient.ResponseProcessor;
import com.webfluxclient.codec.ExtendedClientCodecConfigurer;

import java.lang.reflect.InvocationHandler;
import java.net.URI;
import java.util.List;

public interface ReactiveInvocationHandlerFactory {
    InvocationHandler build(
            ExtendedClientCodecConfigurer codecConfigurer,
            List<RequestProcessor> requestProcessors,
            List<ResponseProcessor> responseProcessors,
            Class<?> target,
            URI uri);
}
