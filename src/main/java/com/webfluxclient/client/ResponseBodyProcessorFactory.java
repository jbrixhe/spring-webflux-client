package com.webfluxclient.client;

import com.webfluxclient.codec.ExtendedClientCodecConfigurer;

public interface ResponseBodyProcessorFactory {
    ResponseBodyProcessor create(ExtendedClientCodecConfigurer codecs);
}
