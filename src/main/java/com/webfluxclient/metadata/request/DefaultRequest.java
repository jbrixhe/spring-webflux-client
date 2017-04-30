package com.webfluxclient.metadata.request;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DefaultRequest implements Request {
    private UriBuilder uriBuilder;
    private HttpMethod httpMethod;
    private HttpHeaders httpHeaders;
    private Map<String, Object> variables;
    private BodyInserter<?, ? super ClientHttpRequest> bodyInserter;
    
    public DefaultRequest(UriBuilder uriBuilder,
                          HttpMethod httpMethod,
                          HttpHeaders httpHeaders,
                          Map<String, Object> variables,
                          BodyInserter<?, ? super ClientHttpRequest> bodyInserter) {
    
        this.uriBuilder = uriBuilder;
        this.httpMethod = httpMethod;
        this.bodyInserter = bodyInserter;
        this.variables = new HashMap<>();
        this.variables.putAll(variables);
        this.httpHeaders = new HttpHeaders();
        this.httpHeaders.putAll(httpHeaders);
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
        variables.forEach((name, value) -> variables.replace(name, processVariable(value)));
        
        return uriBuilder.build(variables);
    }
    
    private Object processVariable(Object variable) {
        if (Collection.class.isInstance(variable)) {
            return ((Collection<?>) variable).toArray();
        }
        return variable;
    }
}
