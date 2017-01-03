package com.github.jbrixhe.reactiveclient.request;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class ReactiveContext {
    private String url;
    private String path;
}
