package com.webfluxclient.client;

import com.webfluxclient.RequestProcessor;
import com.webfluxclient.ResponseProcessor;
import com.webfluxclient.codec.ExtendedClientCodecConfigurer;

import java.util.List;

public interface RequestExecutorFactory {
    RequestExecutor build(ExtendedClientCodecConfigurer codecConfigurer,
                          List<RequestProcessor> requestProcessors,
                          List<ResponseProcessor> responseProcessors);
}