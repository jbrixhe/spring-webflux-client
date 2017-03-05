package com.reactiveclient.example.server;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException() {
    }

    public DuplicateResourceException(String message) {
        super(message);
    }
}
