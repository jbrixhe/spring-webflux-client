package com.reactiveclient.handler;

import com.reactiveclient.metadata.MethodMetadata;
import com.reactiveclient.metadata.request.ReactiveRequest;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.InvocationHandler;
import java.util.List;
import java.util.function.Consumer;

public interface ReactiveInvocationHandlerFactory {

    InvocationHandler create(List<MethodMetadata> requestTemplates, WebClient webClient, Consumer<ReactiveRequest> requestInterceptor);

}
