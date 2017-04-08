package com.reactiveclient.client.codec;

import com.reactiveclient.DecoderHttpErrorReader;
import com.reactiveclient.ErrorDecoder;
import com.reactiveclient.HttpErrorReader;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ExtendedClientCodecConfigurer extends ClientCodecConfigurer {
    private final ExtendedClientCustomCodecConfigurer customCodec = new ExtendedClientCustomCodecConfigurer();
    private final ExtendedClientDefaultCodecConfigurer defaultCodec = new ExtendedClientDefaultCodecConfigurer();

    public ExtendedClientDefaultCodecConfigurer defaultCodec() {
        return defaultCodec;
    }

    public ExtendedClientCustomCodecConfigurer customCodec(){
        return customCodec;
    }

    public List<HttpErrorReader> getErrorReaders(){
        List<HttpErrorReader> result = new ArrayList<>();

        customCodec().addErrorReaderTo(result);
        addDefaultErrorDecoder(result);

        return result;
    }

    private void addDefaultErrorDecoder(List<HttpErrorReader> errorReaders){
        defaultCodec().addErrorReaderTo(errorReaders, HttpClientErrorDecoder.class, HttpClientErrorDecoder::new);
        defaultCodec().addErrorReaderTo(errorReaders, HttpServerErrorDecoder.class, HttpServerErrorDecoder::new);
    }

    public static class ExtendedClientDefaultCodecConfigurer extends ClientDefaultCodecConfigurer {
        private Map<Class<?>, HttpErrorReader> errorReaders = new HashMap<>();
        private boolean suppressed = false;

        private void setSuppressed(boolean suppressed) {
            this.suppressed = suppressed;
        }

        protected Map<Class<?>, HttpErrorReader> getErrorReaders(){
            return this.errorReaders;
        }

        protected <D extends ErrorDecoder<?>> void addErrorReaderTo(List<HttpErrorReader> result, Class<D> key, Supplier<D> fallback) {
            addErrorReaderTo(result, () -> findErrorDecoderReader(key, fallback));
        }

        protected <D extends ErrorDecoder<?>> HttpErrorReader findErrorDecoderReader(Class<D> decoderType, Supplier<D> fallback) {
            DecoderHttpErrorReader reader = (DecoderHttpErrorReader) this.errorReaders.get(decoderType);
            return reader != null ? reader : new DecoderHttpErrorReader(fallback.get());
        }

        @Override
        protected void addWriterTo(List<HttpMessageWriter<?>> result, Supplier<HttpMessageWriter<?>> writer) {
            addTo(result, writer);
        }

        @Override
        protected void addReaderTo(List<HttpMessageReader<?>> result, Supplier<HttpMessageReader<?>> reader) {
            addTo(result, reader);
        }

        protected void addErrorReaderTo(List<HttpErrorReader> result, Supplier<HttpErrorReader> reader) {
            addTo(result, reader);
        }

        private  <T> void addTo(List<T> result, Supplier<T> reader) {
            if (!this.suppressed) {
                result.add(reader.get());
            }
        }
    }

    public static class ExtendedClientCustomCodecConfigurer extends CustomCodecConfigurer {
        private List<HttpErrorReader> errorReaders = new ArrayList<>();

        public void errorReader(HttpErrorReader errorReader){

            this.errorReaders.add(errorReader);
        }

        public void errorDecoder(ErrorDecoder errorDecoder){
            errorReader(new DecoderHttpErrorReader(errorDecoder));
        }

        protected void addErrorReaderTo(List<HttpErrorReader> result){
            result.addAll(errorReaders);
        }
    }
}
