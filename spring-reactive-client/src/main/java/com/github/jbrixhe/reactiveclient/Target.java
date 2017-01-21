package com.github.jbrixhe.reactiveclient;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.net.URI;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Target<T> {

    private Class<T> type;

    private URI uri;
}
