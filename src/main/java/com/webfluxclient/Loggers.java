package com.webfluxclient;

import java.util.function.Supplier;
import java.util.logging.Level;

public class Loggers {

    public static Logger javaUtil(String name, Level level) {
        return new Logger() {
            private java.util.logging.Logger logger = java.util.logging.Logger.getLogger(name);

            @Override
            public void log(Supplier<String> messageSupplier) {
                logger.log(level, messageSupplier);
            }

            @Override
            public void log(String message) {
                logger.log(level, message);
            }
        };
    }
}
