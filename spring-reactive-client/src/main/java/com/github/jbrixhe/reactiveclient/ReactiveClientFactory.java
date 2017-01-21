package com.github.jbrixhe.reactiveclient;

import com.github.jbrixhe.reactiveclient.handler.InvocationHandlerFactory;
import com.github.jbrixhe.reactiveclient.metadata.MethodMetadata;
import com.github.jbrixhe.reactiveclient.metadata.request.RequestTemplate;
import com.github.jbrixhe.reactiveclient.metadata.RequestTemplateAnnotationVisitor;

import java.lang.reflect.Proxy;
import java.util.List;

public class ReactiveClientFactory implements ClientFactory {

    private RequestTemplateAnnotationVisitor requestTemplateAnnotationVisitor;
    private InvocationHandlerFactory invocationHandlerFactory;

    public ReactiveClientFactory() {
        requestTemplateAnnotationVisitor = new RequestTemplateAnnotationVisitor();
        invocationHandlerFactory = new InvocationHandlerFactory.Default();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T newInstance(Target<T> target) {
        List<MethodMetadata> requestTemplates = requestTemplateAnnotationVisitor.visit(target.getType());

        return (T) Proxy.newProxyInstance(target.getType().getClassLoader(), new Class<?>[]{target.getType()}, invocationHandlerFactory.create(requestTemplates));
    }
}
