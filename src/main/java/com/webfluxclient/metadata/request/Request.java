package com.webfluxclient.metadata.request;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.reactive.function.BodyInserter;

import java.net.URI;
import java.util.Map;

public interface Request {
    
    HttpMethod httpMethod();
    
    HttpHeaders headers();
    
    Map<String, Object> variables();
    
    BodyInserter<?, ? super ClientHttpRequest> bodyInserter();
    
    URI expand();
}
