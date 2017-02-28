package com.reactiveclient.example.server.message;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MessageService {
    Mono<MessageResource> getMessage(Mono<Integer> messageId);

    Flux<MessageResource> getMessages();

    Mono<MessageResource> addMessage(Mono<MessageRequest> newMessage);

    Mono<MessageResource> updateMessage(Integer messageId, Mono<MessageResource> messageToUpdate);
}
