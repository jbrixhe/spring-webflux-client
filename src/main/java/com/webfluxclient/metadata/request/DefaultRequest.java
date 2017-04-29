package com.webfluxclient.metadata.request;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

@AllArgsConstructor
public class DefaultRequest implements Request {
    private UriBuilder uriBuilder;
    private HttpMethod httpMethod;
    private HttpHeaders httpHeaders;
    private Map<String, Object> variables;
    private BodyInserter<?, ? super ClientHttpRequest> bodyInserter;
    
    
    @Override
    public HttpMethod httpMethod() {
        return httpMethod;
    }
    
    @Override
    public HttpHeaders headers() {
        return httpHeaders;
    }
    
    @Override
    public List<String> header(String name) {
        return httpHeaders.get(name);
    }
    
    @Override
    public void addHeader(String name, String... values) {
        addHeader(name, asList(values));
    }
    
    @Override
    public void addHeader(String name, List<String> values) {
        httpHeaders.putIfAbsent(name, values);
    }
    
    @Override
    public Map<String, Object> variables() {
        return variables;
    }
    
    @Override
    public void variable(String name, Object value) {
        variables.putIfAbsent(name, value);
    }
    
    @Override
    public BodyInserter<?, ? super ClientHttpRequest> bodyInserter() {
        return bodyInserter;
    }
    
    @Override
    public URI expand() {
        return uriBuilder.build(variables);
    }
}
