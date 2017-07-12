package com.webfluxclient.client;

import com.webfluxclient.RequestProcessor;
import com.webfluxclient.ResponseInterceptor;
import com.webfluxclient.codec.ExtendedClientCodecConfigurer;

import java.util.List;

public interface RequestExecutorFactory {
    RequestExecutor build(ExtendedClientCodecConfigurer codecConfigurer,
                          List<RequestProcessor> requestProcessors,
                          List<ResponseInterceptor> responseInterceptors);
}