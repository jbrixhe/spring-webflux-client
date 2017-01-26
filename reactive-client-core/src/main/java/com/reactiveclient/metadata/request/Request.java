package com.reactiveclient.metadata.request;

import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.net.URI;

@Data
public class Request {
    private URI uri;
    private HttpHeaders httpHeaders;
    private HttpMethod httpMethod;
    private Object body;
}
