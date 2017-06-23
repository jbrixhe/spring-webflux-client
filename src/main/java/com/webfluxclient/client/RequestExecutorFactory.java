package com.webfluxclient.client;

import com.webfluxclient.RequestInterceptor;
import com.webfluxclient.codec.ExtendedClientCodecConfigurer;

public interface RequestExecutorFactory {
    RequestExecutor create(ExtendedClientCodecConfigurer codecs, RequestInterceptor requestInterceptor);
}