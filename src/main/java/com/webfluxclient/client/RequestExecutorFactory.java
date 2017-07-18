package com.webfluxclient.client;

import com.webfluxclient.codec.ExtendedClientCodecConfigurer;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

public interface RequestExecutorFactory {
    RequestExecutor build(ExtendedClientCodecConfigurer codecConfigurer,
                          ExchangeFilterFunction exchangeFilterFunction);
}