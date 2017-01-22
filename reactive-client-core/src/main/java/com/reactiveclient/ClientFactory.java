package com.reactiveclient;

public interface ClientFactory {

    <T> T newInstance(Target<T> target);
}
