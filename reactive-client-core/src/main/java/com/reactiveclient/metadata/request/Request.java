package com.reactiveclient.metadata.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.net.URI;

@Setter(AccessLevel.PACKAGE)
@Getter
public class Request {
    private URI uri;
    private HttpHeaders httpHeaders;
    private HttpMethod httpMethod;
    private Object body;
}
