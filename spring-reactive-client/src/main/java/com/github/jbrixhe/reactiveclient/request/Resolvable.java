package com.github.jbrixhe.reactiveclient.request;

import java.util.Map;

public interface Resolvable {
    String resolve(Map<String,Object> parameters);
}
