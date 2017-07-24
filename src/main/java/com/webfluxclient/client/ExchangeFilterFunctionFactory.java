package com.webfluxclient.client;

import com.webfluxclient.LogLevel;
import com.webfluxclient.Logger;
import com.webfluxclient.RequestProcessor;
import com.webfluxclient.ResponseProcessor;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import java.util.List;

public interface ExchangeFilterFunctionFactory {
    ExchangeFilterFunction build(List<RequestProcessor> requestProcessors,
                                 List<ResponseProcessor> responseProcessors,
                                 Logger logger,
                                 LogLevel logLevel);
}
