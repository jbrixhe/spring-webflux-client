package com.webfluxclient.codec;

import org.springframework.http.codec.ClientCodecConfigurer;

import java.util.List;

public interface ExtendedClientCodecConfigurer extends ClientCodecConfigurer {

    List<HttpErrorReader> getErrorReaders();

    ExtendedCustomCodecs customCodecs();

    ExtendedDefaultCodecs defaultCodecs();

    static ExtendedClientCodecConfigurer create(){
        return new DefaultExtendedClientCodecConfigurer();
    }

    interface ExtendedDefaultCodecs extends ClientDefaultCodecs {
        void clientErrorDecoder(HttpClientErrorDecoder clientErrorDecoder);
        void serverErrorDecoder(HttpServerErrorDecoder serverErrorDecoder);
    }

    interface ExtendedCustomCodecs extends CustomCodecs{
        void errorReader(HttpErrorReader errorReader);
        void errorDecoder(ErrorDecoder errorDecoder);
    }
}
