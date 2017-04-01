package com.reactiveclient.handler;

import com.reactiveclient.RequestInterceptor;
import com.reactiveclient.metadata.MethodMetadata;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.InvocationHandler;
import java.util.List;

public interface ReactiveInvocationHandlerFactory {

    InvocationHandler create(List<MethodMetadata> requestTemplates, WebClient webClient, List<RequestInterceptor> requestInterceptors);

}
