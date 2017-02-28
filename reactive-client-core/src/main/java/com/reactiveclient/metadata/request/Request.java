package com.reactiveclient.metadata.request;

import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Data
public class Request {
    private URI uri;
    private HttpHeaders httpHeaders;
    private Object body;
}
