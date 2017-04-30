package com.webfluxclient.metadata.request;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;


public class DefaultRequestTest {
    @Test
    public void expand() throws Exception {
        DefaultRequest defaultRequest = buildRequest("http://example.ca/api/users", Collections.emptyMap());
    
        assertThat(defaultRequest.expand())
                .isEqualTo(URI.create("http://example.ca/api/users"));
    }
    
    @Test
    public void expand_withQueryParams() throws Exception {
        Map<String, Object> variables = new HashMap<String, Object>(){{
            put("limit", 45);
            put("page", 3);
        }};
        DefaultRequest defaultRequest = buildRequest("http://example.ca/api/users?limit={limit}&page={page}", variables);
        
        assertThat(defaultRequest.expand())
                .isEqualTo(URI.create("http://example.ca/api/users?limit=45&page=3"));
    }
    
    @Test
    public void expand_withQueryParamsAndPathVariables() throws Exception {
        Map<String, Object> variables = new HashMap<String, Object>(){{
            put("id", 1);
            put("limit", 45);
            put("page", 3);
        }};
        DefaultRequest defaultRequest = buildRequest("http://example.ca/api/users/{id}/contact?limit={limit}&page={page}", variables);
        
        assertThat(defaultRequest.expand())
                .isEqualTo(URI.create("http://example.ca/api/users/1/contact?limit=45&page=3"));
    }
    
    @Test
    public void expand_withOverrideValue() throws Exception {
        Map<String, Object> variables = new HashMap<String, Object>(){{
            put("id", 1);
            put("limit", 45);
            put("page", 3);
        }};
        DefaultRequest defaultRequest = buildRequest("http://example.ca/api/users/{id}/contact?limit={limit}&page={page}", variables);
    
        defaultRequest.variables().put("id", 2);
        
        assertThat(defaultRequest.expand())
                .isEqualTo(URI.create("http://example.ca/api/users/2/contact?limit=45&page=3"));
    }
    
    @Test
    public void expand_withQueryParamsValueIsACollection() throws Exception {
        Map<String, Object> variables = new HashMap<String, Object>(){{
            put("id", 1);
            put("contactIds", Arrays.asList(2,3,4));
        }};
        DefaultRequest defaultRequest = buildRequest("http://example.ca/api/users/{id}/contact?contactIds={contactIds}", variables);
        
        assertThat(defaultRequest.expand())
                .isEqualTo(URI.create("http://example.ca/api/users/1/contact?contactIds=2,3,4"));
    }
    
    @Test
    public void headers_withReadOnlyHttpHeaders() {
        HttpHeaders httpHeaders = HttpHeaders.readOnlyHttpHeaders(new HttpHeaders());
        DefaultRequest defaultRequest = new DefaultRequest(new DefaultUriBuilderFactory().builder(),
                HttpMethod.GET, httpHeaders, new HashMap<>(), BodyInserters.empty());
        defaultRequest.headers().add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        
        assertThat(defaultRequest.headers().getFirst(HttpHeaders.ACCEPT)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    }
    
    @Test
    public void headers_withAddNewHeaderValue() {
        DefaultRequest defaultRequest = buildRequest("http://example.ca/api/users/{id}/contact?contactIds={contactIds}", new HashMap<>());
        defaultRequest.headers().add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        
        assertThat(defaultRequest.headers().getFirst(HttpHeaders.ACCEPT)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    }
    
    @Test
    public void headers_withOverrideHeaderValue() {
        DefaultRequest defaultRequest = buildRequest(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        defaultRequest.headers().put(HttpHeaders.ACCEPT, singletonList(MediaType.APPLICATION_XML_VALUE));
        
        assertThat(defaultRequest.headers().getFirst(HttpHeaders.ACCEPT)).isEqualTo(MediaType.APPLICATION_XML_VALUE);
    }
    
    @Test
    public void overrideHeaderValue_withHttpHeaders() {
        DefaultRequest defaultRequest = buildRequest(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        defaultRequest.headers().put(HttpHeaders.ACCEPT, singletonList(MediaType.APPLICATION_XML_VALUE));
        
        assertThat(defaultRequest.headers().getFirst(HttpHeaders.ACCEPT)).isEqualTo(MediaType.APPLICATION_XML_VALUE);
    }
    
    public DefaultRequest buildRequest(String uri, Map<String, Object> variables) {
        UriBuilder uriBuilder = new DefaultUriBuilderFactory(uri).builder();
        return new DefaultRequest(uriBuilder, HttpMethod.GET, new HttpHeaders(), variables, BodyInserters.empty());
    }
    
    public DefaultRequest buildRequest(String headerName, String headerValue) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(headerName, headerValue);
        
        return new DefaultRequest(new DefaultUriBuilderFactory().builder(),
                HttpMethod.GET, httpHeaders, new HashMap<>(), BodyInserters.empty());
    }
}