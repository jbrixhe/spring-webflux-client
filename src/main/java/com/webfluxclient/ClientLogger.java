package com.webfluxclient;

import java.util.function.Supplier;

public interface ClientLogger {
    void log(Supplier<String> messageSupplier);
}
