package com.reactiveclient.client;

import com.reactiveclient.HttpErrorReader;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

import java.util.function.Supplier;
import java.util.stream.Stream;

interface ExtendedExchangeStrategies extends ExchangeStrategies {

    Supplier<Stream<HttpErrorReader>> exceptionReader();

}
