package com.webfluxclient.handler;

import com.webfluxclient.RequestInterceptor;
import com.webfluxclient.metadata.MethodMetadata;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.InvocationHandler;
import java.util.List;

public interface ReactiveInvocationHandlerFactory {

    InvocationHandler create(List<MethodMetadata> requestTemplates, WebClient webClient, RequestInterceptor requestInterceptor);

}
