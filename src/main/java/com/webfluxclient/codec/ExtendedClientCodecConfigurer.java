package com.webfluxclient.codec;

import org.springframework.http.codec.ClientCodecConfigurer;

import java.util.List;

public interface ExtendedClientCodecConfigurer extends ClientCodecConfigurer {

    List<HttpErrorReader> getErrorReaders();

    ExtendedCustomCodecs customCodecs();

    ExtendedClientDefaultCodecs defaultCodecs();

    static ExtendedClientCodecConfigurer create(){
        return new DefaultExtendedClientCodecConfigurer();
    }

    interface ExtendedClientDefaultCodecs extends ClientDefaultCodecs {
        void httpClientErrorDecoder(HttpClientErrorDecoder clientErrorDecoder);
        void httpServerErrorDecoder(HttpServerErrorDecoder serverErrorDecoder);
    }

    interface ExtendedCustomCodecs extends CustomCodecs{
        void errorReader(HttpErrorReader errorReader);
        void errorDecoder(ErrorDecoder errorDecoder);
    }
}
