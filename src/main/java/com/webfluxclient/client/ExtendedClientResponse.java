package com.webfluxclient.client;

import org.reactivestreams.Publisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Supplier;
import java.util.stream.Stream;

class ExtendedClientResponse implements ClientResponse {
    private final ClientHttpResponse response;
    
    private final Headers headers;
    
    private final ExtendedExchangeStrategies strategies;
    
    ExtendedClientResponse(ClientHttpResponse response, ExtendedExchangeStrategies strategies) {
        this.response = response;
        this.strategies = strategies;
        this.headers = new DefaultHeaders();
    }
    
    @Override
    public HttpStatus statusCode() {
        return this.response.getStatusCode();
    }
    
    @Override
    public Headers headers() {
        return this.headers;
    }
    
    @Override
    public MultiValueMap<String, ResponseCookie> cookies() {
        return response.getCookies();
    }
    
    @Override
    public <T> T body(BodyExtractor<T, ? super ClientHttpResponse> extractor) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public <T> Mono<T> bodyToMono(Class<? extends T> elementClass) {
        return bodyToPublisher(BodyExtractors.toMono(elementClass), ErrorExtractors.toMono());
    }
    
    @Override
    public <T> Flux<T> bodyToFlux(Class<? extends T> elementClass) {
        return bodyToPublisher(BodyExtractors.toFlux(elementClass), ErrorExtractors.toFlux());
    }
    
    private <T extends Publisher<?>> T bodyToPublisher(BodyExtractor<T, ? super ClientHttpResponse> bodyExtractor,
                                                       ErrorExtractor<T, ? super ClientHttpResponse> errorExtractor) {
        
        HttpStatus status = statusCode();
        if (status.is4xxClientError() || status.is5xxServerError()) {
            return errorExtractor
                    .extract(response, strategies::exceptionReader);
        }
        else {
            return bodyExtractor.extract(this.response, new BodyExtractor.Context() {
                @Override
                public Supplier<Stream<HttpMessageReader<?>>> messageReaders() {
                    return strategies.messageReaders();
                }
                
                @Override
                public Optional<ServerHttpResponse> serverResponse() {
                    return Optional.empty();
                }
                
                @Override
                public Map<String, Object> hints() {
                    return Collections.emptyMap();
                }
            });
        }
    }
    
    private class DefaultHeaders implements Headers {
        
        private HttpHeaders delegate() {
            return response.getHeaders();
        }
        
        @Override
        public OptionalLong contentLength() {
            return toOptionalLong(delegate().getContentLength());
        }
        
        @Override
        public Optional<MediaType> contentType() {
            return Optional.ofNullable(delegate().getContentType());
        }
        
        @Override
        public List<String> header(String headerName) {
            List<String> headerValues = delegate().get(headerName);
            return headerValues != null ? headerValues : Collections.emptyList();
        }
        
        @Override
        public HttpHeaders asHttpHeaders() {
            return HttpHeaders.readOnlyHttpHeaders(delegate());
        }
        
        private OptionalLong toOptionalLong(long value) {
            return value != -1 ? OptionalLong.of(value) : OptionalLong.empty();
        }
        
    }
}
