package com.webfluxclient.codec;

import org.springframework.http.codec.ClientCodecConfigurer;

import java.util.List;

public interface ExtendedClientCodecConfigurer extends ClientCodecConfigurer {

    List<HttpErrorReader> getErrorReaders();

    ExtendedCustomCodecsConfigurer customCodecs();

    ExtendedClientDefaultCodecsConfigurer defaultCodecs();

    static ExtendedClientCodecConfigurer create(){
        return new DefaultExtendedClientCodecConfigurer();
    }

    interface ExtendedClientDefaultCodecsConfigurer extends ClientDefaultCodecsConfigurer {
        void clientErrorDecoder(HttpClientErrorDecoder clientErrorDecoder);
        void serverErrorDecoder(HttpServerErrorDecoder serverErrorDecoder);
    }

    interface ExtendedCustomCodecsConfigurer extends CustomCodecsConfigurer{
        void errorReader(HttpErrorReader errorReader);
        void errorDecoder(ErrorDecoder errorDecoder);
    }
}
