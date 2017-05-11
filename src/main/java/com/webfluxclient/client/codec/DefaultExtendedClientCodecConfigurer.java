package com.webfluxclient.client.codec;

import com.webfluxclient.DecoderHttpErrorReader;
import com.webfluxclient.ErrorDecoder;
import com.webfluxclient.HttpErrorReader;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.DecoderHttpMessageReader;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

class DefaultExtendedClientCodecConfigurer implements ExtendedClientCodecConfigurer {
    private ClientCodecConfigurer clientCodecConfigurer = ClientCodecConfigurer.create();
    private ExtendedCustomCodecsConfigurer extendedCustomCodecsConfigurer = new ExtendedCustomCodecsConfigurer(clientCodecConfigurer.customCodecs());
    private ExtendedClientDefaultCodecsConfigurer extendedClientDefaultCodecsConfigurer = new ExtendedClientDefaultCodecsConfigurer(clientCodecConfigurer.defaultCodecs());
    @Override
    public void registerDefaults(boolean registerDefaults) {
        clientCodecConfigurer.registerDefaults(registerDefaults);
        extendedClientDefaultCodecsConfigurer.setSuppressed(!registerDefaults);
    }

    @Override
    public ExtendedClientCodecConfigurer.ExtendedClientDefaultCodecsConfigurer defaultCodecs() {
        return extendedClientDefaultCodecsConfigurer;
    }

    @Override
    public ExtendedClientCodecConfigurer.ExtendedCustomCodecsConfigurer customCodecs() {
        return extendedCustomCodecsConfigurer;
    }

    @Override
    public List<HttpErrorReader> getErrorReaders() {
        List<HttpErrorReader> errorReaders = new ArrayList<>();

        extendedCustomCodecsConfigurer.addErrorReadersTo(errorReaders);
        extendedClientDefaultCodecsConfigurer.addTypedReadersTo(errorReaders);

        return errorReaders;
    }

    @Override
    public List<HttpMessageReader<?>> getReaders() {
        return clientCodecConfigurer.getReaders();
    }

    @Override
    public List<HttpMessageWriter<?>> getWriters() {
        return clientCodecConfigurer.getWriters();
    }

    private class ExtendedClientDefaultCodecsConfigurer implements ExtendedClientCodecConfigurer.ExtendedClientDefaultCodecsConfigurer{
        private boolean suppressed = false;
        private ClientDefaultCodecsConfigurer codecsConfigurer;
        private Map<Class<?>, HttpErrorReader> errorReaders = new HashMap<>();

        private ExtendedClientDefaultCodecsConfigurer(ClientDefaultCodecsConfigurer codecsConfigurer){
            this.codecsConfigurer = codecsConfigurer;
        }


        private void setSuppressed(boolean suppressed) {
            this.suppressed = suppressed;
        }

        private void addTypedReadersTo(List<HttpErrorReader> result) {
            addErrorReaderTo(result, HttpClientErrorDecoder.class, HttpClientErrorDecoder::new);
            addErrorReaderTo(result, HttpServerErrorDecoder.class, HttpServerErrorDecoder::new);
        }

        private <D extends ErrorDecoder<?>> void addErrorReaderTo(List<HttpErrorReader> result, Class<D> key, Supplier<D> fallback) {
            addErrorReaderTo(result, () -> findErrorDecoderReader(key, fallback));
        }

        private <D extends ErrorDecoder<?>> HttpErrorReader findErrorDecoderReader(Class<D> decoderType, Supplier<D> fallback) {
            HttpErrorReader reader = this.errorReaders.get(decoderType);
            return reader != null ? reader : new DecoderHttpErrorReader(fallback.get());
        }

        private void addErrorReaderTo(List<HttpErrorReader> result, Supplier<HttpErrorReader> reader) {
            if (!this.suppressed) {
                result.add(reader.get());
            }
        }

        @Override
        public void jackson2Decoder(Jackson2JsonDecoder jackson2JsonDecoder) {
            codecsConfigurer.jackson2Decoder(jackson2JsonDecoder);
        }

        @Override
        public void jackson2Encoder(Jackson2JsonEncoder jackson2JsonEncoder) {
            codecsConfigurer.jackson2Encoder(jackson2JsonEncoder);
        }

        @Override
        public void clientErrorDecoder(HttpClientErrorDecoder clientErrorDecoder) {
            this.errorReaders.put(HttpClientErrorDecoder.class, new DecoderHttpErrorReader(clientErrorDecoder));
        }

        @Override
        public void serverErrorDecoder(HttpServerErrorDecoder serverErrorDecoder) {
            this.errorReaders.put(HttpServerErrorDecoder.class, new DecoderHttpErrorReader(serverErrorDecoder));
        }

        @Override
        public void serverSentEventDecoder(Decoder<?> decoder) {
            codecsConfigurer.serverSentEventDecoder(decoder);
        }
    }

    private class ExtendedCustomCodecsConfigurer implements ExtendedClientCodecConfigurer.ExtendedCustomCodecsConfigurer {
        private CustomCodecsConfigurer customCodecsConfigurer;
        private List<HttpErrorReader> customErrorReaders;

        private ExtendedCustomCodecsConfigurer(CustomCodecsConfigurer customCodecsConfigurer){
            this.customCodecsConfigurer = customCodecsConfigurer;
            this.customErrorReaders = new ArrayList<>();
        }

        @Override
        public void errorReader(HttpErrorReader errorReader) {
            customErrorReaders.add(errorReader);
        }

        @Override
        public void errorDecoder(ErrorDecoder errorDecoder) {
            errorReader(new DecoderHttpErrorReader(errorDecoder));
        }

        @Override
        public void decoder(Decoder<?> decoder) {
            customCodecsConfigurer.decoder(decoder);
        }

        @Override
        public void encoder(Encoder<?> encoder) {
            customCodecsConfigurer.encoder(encoder);
        }

        @Override
        public void reader(HttpMessageReader<?> httpMessageReader) {
            customCodecsConfigurer.reader(httpMessageReader);
        }

        @Override
        public void writer(HttpMessageWriter<?> httpMessageWriter) {
            customCodecsConfigurer.writer(httpMessageWriter);
        }

        private void addErrorReadersTo(List<HttpErrorReader> result) {
            result.addAll(customErrorReaders);
        }
    }
}
