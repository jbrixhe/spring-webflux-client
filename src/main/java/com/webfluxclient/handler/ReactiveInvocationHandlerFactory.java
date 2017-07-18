package com.webfluxclient.handler;

import com.webfluxclient.LogLevel;
import com.webfluxclient.Logger;
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
            Logger logger,
            LogLevel logLevel,
            Class<?> target,
            URI uri);
}
