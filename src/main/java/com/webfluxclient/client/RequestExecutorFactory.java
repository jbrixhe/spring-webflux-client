package com.webfluxclient.client;

import com.webfluxclient.codec.ExtendedClientCodecConfigurer;

public interface RequestExecutorFactory {
    RequestExecutor create(ExtendedClientCodecConfigurer codecs);
}