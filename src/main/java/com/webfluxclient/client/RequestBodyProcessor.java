package com.webfluxclient.client;

import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.reactive.function.BodyInserter;

public interface RequestBodyProcessor {
    BodyInserter<?, ? super ClientHttpRequest> process(Object body);
}
