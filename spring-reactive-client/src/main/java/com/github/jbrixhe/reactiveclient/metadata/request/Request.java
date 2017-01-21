package com.github.jbrixhe.reactiveclient.metadata.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.net.URI;

@Getter
@AllArgsConstructor(staticName = "create")
public class Request {
    private URI uri;
    private HttpHeaders httpHeaders;
    private HttpMethod httpMethod;
}
