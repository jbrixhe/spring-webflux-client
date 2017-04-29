package com.webfluxclient.metadata.request;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.reactive.function.BodyInserter;

import java.net.URI;
import java.util.List;
import java.util.Map;

public interface Request {
    
    HttpMethod httpMethod();
    
    HttpHeaders headers();
    
    List<String> header(String name);
    
    void addHeader(String name, String... values);
    
    void addHeader(String name, List<String> values);
    
    Map<String, Object> variables();
    
    void variable(String name, Object value);
    
    BodyInserter<?, ? super ClientHttpRequest> bodyInserter();
    
    URI expand();
}
