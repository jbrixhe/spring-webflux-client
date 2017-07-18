package com.webfluxclient.client;

import com.webfluxclient.LogLevel;
import com.webfluxclient.Logger;
import com.webfluxclient.RequestProcessor;
import com.webfluxclient.ResponseProcessor;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import java.util.List;

import static com.webfluxclient.client.ExchangeFilterFunctions.requestProcessorFilter;
import static com.webfluxclient.client.ExchangeFilterFunctions.responseInterceptorFilter;

public class DefaultExchangeFilterFunctionFactory implements ExchangeFilterFunctionFactory {

    @Override
    public ExchangeFilterFunction build(List<RequestProcessor> requestProcessors, List<ResponseProcessor> responseProcessors, Logger logger, LogLevel logLevel) {
        ExchangeFilterFunction exchangeFilterFunction = null;
        if (!requestProcessors.isEmpty()) {
            RequestProcessor requestProcessor = requestProcessors.stream()
                    .reduce(RequestProcessor::andThen)
                    .orElseGet(() -> clientRequest -> clientRequest);

            exchangeFilterFunction = requestProcessorFilter(requestProcessor);
        }

        if (logger != null && !LogLevel.NONE.equals(logLevel)) {
            ExchangeFilterFunction loggingExchangeFilterFunction = ExchangeFilterFunctions.loggingFilter(logger, logLevel);
            exchangeFilterFunction = exchangeFilterFunction == null?
                    loggingExchangeFilterFunction :
                    exchangeFilterFunction.andThen(loggingExchangeFilterFunction);
        }

        if (!responseProcessors.isEmpty()) {
            ResponseProcessor responseProcessor = responseProcessors.stream()
                    .reduce(ResponseProcessor::andThen)
                    .orElseGet(() -> clientResponse -> clientResponse);

            ExchangeFilterFunction responseInterceptorFilter = responseInterceptorFilter(responseProcessor);
            exchangeFilterFunction = exchangeFilterFunction == null?
                    responseInterceptorFilter :
                    exchangeFilterFunction.andThen(responseInterceptorFilter);
        }

        return exchangeFilterFunction;
    }
}
