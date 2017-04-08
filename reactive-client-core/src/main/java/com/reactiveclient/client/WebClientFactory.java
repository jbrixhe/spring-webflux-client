package com.reactiveclient.client;

import com.reactiveclient.client.codec.ExtendedClientCodecConfigurer;
import org.springframework.web.reactive.function.client.WebClient;

public interface WebClientFactory {
    WebClient create(ExtendedClientCodecConfigurer codecs);
}