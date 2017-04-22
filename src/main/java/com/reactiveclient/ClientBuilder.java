package com.reactiveclient;

import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;

import java.net.URI;

/**
 *  A mutable builder to configure a Proxy
 *
 * @author Jérémy Brixhe
 * */
public interface ClientBuilder {

    ClientBuilder registerDefaultCodecs(boolean registerDefaults);

    /**
     * Add the given {@link ErrorDecoder} to this builder. This is a convenient alternative to adding a
     * {@link DecoderHttpErrorReader} that wraps the given decoder.
     * @param errorDecoder the error decoder to add
     * @return this builder
     */
    ClientBuilder errorDecoder(ErrorDecoder errorDecoder);

    /**
     * Add the given {@link HttpErrorReader} to this builder.
     * @param httpErrorReader the decoder to add
     * @return this builder
     */
    ClientBuilder errorReader(HttpErrorReader httpErrorReader);

    /**
     * Add the given {@link Decoder} to this builder. This is a convenient alternative to adding a
     * {@link org.springframework.http.codec.DecoderHttpMessageReader} that wraps the given decoder.
     * @param decoder the decoder to add
     * @return this builder
     */
    ClientBuilder decoder(Decoder<?> decoder);

    /**
     * Add the given {@link HttpMessageReader} to this builder.
     * @param httpMessageReader the decoder to add
     * @return this builder
     */
    ClientBuilder messageReader(HttpMessageReader<?> httpMessageReader);

    /**
     * Add the given {@link Encoder} to this builder. This is a convenient alternative to adding a
     * {@link org.springframework.http.codec.EncoderHttpMessageWriter} that wraps the given decoder.
     * @param encoder the encoder to add
     * @return this builder
     */
    ClientBuilder encoder(Encoder<?> encoder);

    /**
     * Add the given {@link HttpMessageWriter} to this builder.
     * @param httpMessageWriter the decoder to add
     * @return this builder
     */
    ClientBuilder messageWriter(HttpMessageWriter<?> httpMessageWriter);

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
        return new DefaultClientBuilder();
    }

    static <T> T defaults(Class<T> target, URI uri) {
        return new DefaultClientBuilder()
                .build(target, uri);
    }
}
