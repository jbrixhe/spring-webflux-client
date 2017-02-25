package com.reactiveclient.handler;

import com.reactiveclient.metadata.MethodMetadata;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface ReactiveInvocationHandlerFactory {

    InvocationHandler create(List<MethodMetadata> requestTemplates, WebClient webClient);

}
