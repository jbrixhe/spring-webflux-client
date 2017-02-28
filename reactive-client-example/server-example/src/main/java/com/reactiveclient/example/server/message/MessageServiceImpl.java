package com.reactiveclient.example.server.message;

import com.reactiveclient.example.server.ResourceNotFoundException;
import com.reactiveclient.example.server.message.persistence.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MessageServiceImpl implements MessageService {

    private MessageRepository messageRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Mono<MessageResource> getMessage(Mono<Integer> messageId) {
        return messageId
                .then(messageRepository::findOne)
                .map(MessageMapper::mapToResource)
                .otherwiseIfEmpty(Mono.error(new ResourceNotFoundException()));
    }

    @Transactional(readOnly = true)
    @Override
    public Flux<MessageResource> getMessages() {
        return messageRepository.findAll()
                .map(MessageMapper::mapToResource);
    }

    @Transactional
    @Override
    public Mono<MessageResource> addMessage(Mono<MessageRequest> newMessage) {
        return newMessage.map(MessageMapper::mapToEntity)
                .then(messageRepository::save)
                .map(MessageMapper::mapToResource);
    }

    @Transactional
    @Override
    public Mono<MessageResource> updateMessage(Integer messageId, Mono<MessageResource> messageToUpdate) {
        return messageToUpdate.and(messageResource -> messageRepository.findOne(messageId), MessageMapper::merge);
    }
}
