package com.reactiveclient.handler.client;

import org.springframework.web.reactive.function.client.ExchangeStrategies;

import java.util.function.Supplier;
import java.util.stream.Stream;

public interface ExtendedExchangeStrategies extends ExchangeStrategies {

    Supplier<Stream<HttpExceptionReader>> exceptionReader();

}
