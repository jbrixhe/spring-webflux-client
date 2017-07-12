package com.webfluxclient.client;

import com.webfluxclient.RequestInterceptor;
import com.webfluxclient.ResponseInterceptor;
import com.webfluxclient.codec.ExtendedClientCodecConfigurer;

import java.util.List;

public interface RequestExecutorFactory {
    RequestExecutor build(ExtendedClientCodecConfigurer codecConfigurer,
                          List<RequestInterceptor> requestInterceptors,
                          List<ResponseInterceptor> responseInterceptors);
}