package com.github.jbrixhe.reactiveclient;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Target<T> {

    private Class<T> type;

    private String url;
}
