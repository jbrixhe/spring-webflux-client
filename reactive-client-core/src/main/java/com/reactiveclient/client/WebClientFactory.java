package com.reactiveclient.client;

import com.reactiveclient.ErrorDecoder;
import com.reactiveclient.RequestInterceptor;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public interface WebClientFactory {
    WebClient create(List<ErrorDecoder> errorDecoders, RequestInterceptor requestInterceptor);
}