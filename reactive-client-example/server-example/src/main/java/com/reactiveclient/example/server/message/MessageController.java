package com.reactiveclient.example.server.message;

import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
public class MessageController {

    private MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/messages/{id}")
    public Mono<MessageResource> getMessage(Mono<Integer> messageId) {
        return messageService.getMessage(messageId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/messages")
    public Flux<MessageResource> getMessages() {
        return messageService.getMessages();
    }

    @RequestMapping(method = RequestMethod.POST, path = "/messages")
    public Mono<MessageResource> addMessage(@Valid Mono<MessageRequest> newMessage) {
        return messageService.addMessage(newMessage);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/messages/{id}")
    public Mono<MessageResource> updateMessage(@PathVariable("id") Integer messageId, @Valid Mono<MessageResource> newMessage) {
        return messageService.updateMessage(messageId, newMessage);
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public Mono<String> handleException(Exception e) {
        return Mono.error(e);
    }
}
