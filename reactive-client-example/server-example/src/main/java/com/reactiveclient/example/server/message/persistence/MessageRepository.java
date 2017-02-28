package com.reactiveclient.example.server.message.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MessageRepository extends ReactiveCrudRepository<Message, Integer> {
}
