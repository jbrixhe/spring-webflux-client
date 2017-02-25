package com.reactiveclient.client;

import org.springframework.web.reactive.function.client.ExchangeStrategies;

import java.util.function.Supplier;
import java.util.stream.Stream;

interface ExtendedExchangeStrategies extends ExchangeStrategies {

    Supplier<Stream<HttpExceptionReader>> exceptionReader();

}
