package com.webfluxclient;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientLoggers {

    public static ClientLogger javaUtil(String name, Level level) {
        return new ClientLogger() {
            private Logger logger = Logger.getLogger(name);

            @Override
            public void log(Supplier<String> messageSupplier) {
                logger.log(level, messageSupplier);
            }
        };
    }

    public static ClientLogger empty() {
        return message -> {};
    }
}
