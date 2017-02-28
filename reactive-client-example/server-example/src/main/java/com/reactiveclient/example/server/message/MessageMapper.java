package com.reactiveclient.example.server.message;

import com.reactiveclient.example.server.message.persistence.Message;

public class MessageMapper {

    public static MessageResource mapToResource(Message message){
        return new MessageResource(message.getId(), message.getContent());
    }

    public static Message mapToEntity(MessageRequest messageRequest) {
        Message message = new Message();
        message.setContent(messageRequest.getContent());
        return message;
    }

    static MessageResource merge(MessageResource messageResource, Message message) {
        message.setContent(messageResource.getContent());
        return messageResource;
    }
}
