package com.webfluxclient;

import com.webfluxclient.codec.ExtendedClientCodecConfigurer;
import com.webfluxclient.handler.DefaultReactiveInvocationHandlerFactory;

import java.net.URI;
import java.util.function.Consumer;

/**
 *  A mutable builder to configure a Proxy
 *
 * @author Jérémy Brixhe
 * */
public interface ClientBuilder {

    ClientBuilder registerDefaultCodecs(boolean registerDefaults);

    ClientBuilder defaultCodecs(Consumer<ExtendedClientCodecConfigurer.ExtendedClientDefaultCodecsConfigurer> defaultCodecsConfigurerConsumer);

    ClientBuilder customCodecs(Consumer<ExtendedClientCodecConfigurer.ExtendedCustomCodecsConfigurer> customCodecsConfigurerConsumer);


    /**
     * Add a {@link RequestInterceptor} to the builder.
     *
     * @param requestInterceptor The request consumer to use.
     * @return this builder
     * */
    ClientBuilder requestInterceptor(RequestInterceptor requestInterceptor);

    /**
     * Build the proxy instance
     *
     * @param target The interface class to initialize the new proxy.
     * @param uri The base Uri for all request
     * @return a configured Porxy for the target class
     * */
    <T> T build(Class<T> target, URI uri);


    /**
     * Return a mutable builder with the default initialization.
     */
    static ClientBuilder builder(){
        return new DefaultClientBuilder(new DefaultReactiveInvocationHandlerFactory());
    }

    static <T> T defaults(Class<T> target, URI uri) {
        return builder()
                .build(target, uri);
    }
}
