package com.webfluxclient.client;

import com.webfluxclient.client.codec.ExtendedClientCodecConfigurer;
import org.springframework.web.reactive.function.client.WebClient;

public interface WebClientFactory {
    WebClient create(ExtendedClientCodecConfigurer codecs);
}