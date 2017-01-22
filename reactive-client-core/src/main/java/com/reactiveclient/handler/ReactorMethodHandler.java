package com.reactiveclient.handler;

import com.reactiveclient.metadata.MethodMetadata;
import com.reactiveclient.metadata.request.Request;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

public class ReactorMethodHandler implements MethodHandler {

    private MethodMetadata methodMetadata;

    public ReactorMethodHandler(MethodMetadata methodMetadata) {
        this.methodMetadata = methodMetadata;
    }

    @Override
    public Object invoke(Object[] args) {
        Request request = methodMetadata.getRequestTemplate().apply(args);
        WebClient client = WebClient.create(new ReactorClientHttpConnector());
        ClientRequest<Void> clientRequest = ClientRequest.method(request.getHttpMethod(), request.getUri())
                .headers(request.getHttpHeaders())
                .build();

        return methodMetadata
                .getReturnType()
                .convert(client.exchange(clientRequest));
    }
}
