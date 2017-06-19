package com.webfluxclient.codec;

import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.ClientCodecConfigurer;
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
    private DefaultExtendedCustomCodecs extendedCustomCodecConfigurer = new DefaultExtendedCustomCodecs(clientCodecConfigurer.customCodecs());
    private ExtendedClientDefaultCodecsImpl defaultExtendedClientCodecConfigurer = new ExtendedClientDefaultCodecsImpl(clientCodecConfigurer.defaultCodecs());
    @Override
    public void registerDefaults(boolean registerDefaults) {
        clientCodecConfigurer.registerDefaults(registerDefaults);
        defaultExtendedClientCodecConfigurer.setSuppressed(!registerDefaults);
    }

    @Override
    public ExtendedClientDefaultCodecs defaultCodecs() {
        return defaultExtendedClientCodecConfigurer;
    }

    @Override
    public ExtendedClientCodecConfigurer.ExtendedCustomCodecs customCodecs() {
        return extendedCustomCodecConfigurer;
    }

    @Override
    public List<HttpErrorReader> getErrorReaders() {
        List<HttpErrorReader> errorReaders = new ArrayList<>();

        extendedCustomCodecConfigurer.addErrorReadersTo(errorReaders);
        defaultExtendedClientCodecConfigurer.addTypedReadersTo(errorReaders);

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

    private class ExtendedClientDefaultCodecsImpl implements ExtendedClientDefaultCodecs {
        private boolean suppressed = false;
        private ClientDefaultCodecs clientDefaultCodecs;
        private Map<Class<?>, HttpErrorReader> errorReaders = new HashMap<>();

        private ExtendedClientDefaultCodecsImpl(ClientDefaultCodecs clientDefaultCodecs){
            this.clientDefaultCodecs = clientDefaultCodecs;
        }

        @Override
        public void jackson2Decoder(Jackson2JsonDecoder jackson2JsonDecoder) {
            clientDefaultCodecs.jackson2Decoder(jackson2JsonDecoder);
        }

        @Override
        public void jackson2Encoder(Jackson2JsonEncoder jackson2JsonEncoder) {
            clientDefaultCodecs.jackson2Encoder(jackson2JsonEncoder);
        }

        @Override
        public void httpClientErrorDecoder(HttpClientErrorDecoder clientErrorDecoder) {
            this.errorReaders.put(HttpClientErrorDecoder.class, new DecoderHttpErrorReader(clientErrorDecoder));
        }

        @Override
        public void httpServerErrorDecoder(HttpServerErrorDecoder serverErrorDecoder) {
            this.errorReaders.put(HttpServerErrorDecoder.class, new DecoderHttpErrorReader(serverErrorDecoder));
        }

        @Override
        public MultipartCodecs multipartCodecs() {
            return clientDefaultCodecs.multipartCodecs();
        }

        @Override
        public void serverSentEventDecoder(Decoder<?> decoder) {
            clientDefaultCodecs.serverSentEventDecoder(decoder);
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
    }

    private class DefaultExtendedCustomCodecs implements ExtendedClientCodecConfigurer.ExtendedCustomCodecs {
        private CustomCodecs customCodecs;
        private List<HttpErrorReader> customErrorReaders;

        private DefaultExtendedCustomCodecs(CustomCodecs customCodecs){
            this.customCodecs = customCodecs;
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
            customCodecs.decoder(decoder);
        }

        @Override
        public void encoder(Encoder<?> encoder) {
            customCodecs.encoder(encoder);
        }

        @Override
        public void reader(HttpMessageReader<?> httpMessageReader) {
            customCodecs.reader(httpMessageReader);
        }

        @Override
        public void writer(HttpMessageWriter<?> httpMessageWriter) {
            customCodecs.writer(httpMessageWriter);
        }

        private void addErrorReadersTo(List<HttpErrorReader> result) {
            result.addAll(customErrorReaders);
        }
    }
}
