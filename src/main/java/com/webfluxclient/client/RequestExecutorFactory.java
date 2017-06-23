package com.webfluxclient.client;

import com.webfluxclient.RequestInterceptor;
import com.webfluxclient.codec.ExtendedClientCodecConfigurer;

import java.util.List;

public interface RequestExecutorFactory {
    RequestExecutor build(ExtendedClientCodecConfigurer codecs, List<RequestInterceptor> requestInterceptors);
}