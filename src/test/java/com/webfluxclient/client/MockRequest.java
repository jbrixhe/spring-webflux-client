package com.webfluxclient.client;

import com.webfluxclient.metadata.request.Request;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class MockRequest implements Request {
    private URI uri;
    private HttpMethod httpMethod;
    private HttpHeaders httpHeaders;
    private Map<String, Object> variables;
    private BodyInserter<?, ? super ClientHttpRequest> bodyInserter;
    
    public MockRequest(String uri, HttpMethod httpMethod) {
        this.uri = URI.create(uri);
        this.httpMethod = httpMethod;
        this.httpHeaders = new HttpHeaders();
        this.variables = new HashMap<>();
        this.bodyInserter = BodyInserters.empty();
    }
    
    @Override
    public HttpMethod httpMethod() {
        return httpMethod;
    }
    
    @Override
    public HttpHeaders headers() {
        return httpHeaders;
    }
    
    @Override
    public Map<String, Object> variables() {
        return variables;
    }
    
    @Override
    public BodyInserter<?, ? super ClientHttpRequest> bodyInserter() {
        return bodyInserter;
    }
    
    @Override
    public URI expand() {
        return uri;
    }
}
