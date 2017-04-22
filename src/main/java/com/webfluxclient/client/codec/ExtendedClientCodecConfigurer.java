package com.webfluxclient.client.codec;

import com.webfluxclient.ErrorDecoder;
import com.webfluxclient.HttpErrorReader;
import org.springframework.http.codec.ClientCodecConfigurer;

import java.util.List;

public interface ExtendedClientCodecConfigurer extends ClientCodecConfigurer {

    List<HttpErrorReader> getErrorReaders();

    ExtendedCustomCodecsConfigurer customCodecs();

    static ExtendedClientCodecConfigurer create(){
        return new DefaultExtendedClientCodecConfigurer();
    }

    interface ExtendedCustomCodecsConfigurer extends CustomCodecsConfigurer{
        void errorReader(HttpErrorReader errorReader);

        void errorDecoder(ErrorDecoder errorDecoder);
    }
}
