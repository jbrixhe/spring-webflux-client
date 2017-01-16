package com.github.jbrixhe.reactiveclient;

public interface ClientFactory {

    <T> T newInstance(Target<T> target);
}
