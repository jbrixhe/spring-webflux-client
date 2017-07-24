package com.webfluxclient;

import java.util.function.Supplier;

public interface Logger {
    void log(Supplier<String> messageSupplier);
    void log(String message);
}
