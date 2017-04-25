package com.webfluxclient.client;

import com.webfluxclient.metadata.request.Request;
import org.springframework.web.reactive.function.client.WebClient;

public interface RequestProcessor {
    Object execute(WebClient webClient, Request request);
}
