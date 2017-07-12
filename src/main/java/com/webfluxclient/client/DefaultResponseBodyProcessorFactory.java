package com.webfluxclient.client;

import com.webfluxclient.codec.ExtendedClientCodecConfigurer;

public class DefaultResponseBodyProcessorFactory implements ResponseBodyProcessorFactory {
    @Override
    public ResponseBodyProcessor create(ExtendedClientCodecConfigurer codecs) {
        return new DefaultResponseBodyProcessor(codecs.getErrorReaders());
    }
}
