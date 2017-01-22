package com.reactiveclient;

import com.reactiveclient.handler.InvocationHandlerFactory;
import com.reactiveclient.metadata.MethodMetadata;
import com.reactiveclient.metadata.MethodMetadataFactory;

import java.lang.reflect.Proxy;
import java.util.List;

public class ReactiveClientFactory implements ClientFactory {

    private MethodMetadataFactory requestTemplateAnnotationVisitor;
    private InvocationHandlerFactory invocationHandlerFactory;

    public ReactiveClientFactory() {
        requestTemplateAnnotationVisitor = new MethodMetadataFactory();
        invocationHandlerFactory = new InvocationHandlerFactory.Default();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T newInstance(Target<T> target) {
        List<MethodMetadata> requestTemplates = requestTemplateAnnotationVisitor.build(target);

        return (T) Proxy.newProxyInstance(target.getType().getClassLoader(), new Class<?>[]{target.getType()}, invocationHandlerFactory.create(requestTemplates));
    }
}
