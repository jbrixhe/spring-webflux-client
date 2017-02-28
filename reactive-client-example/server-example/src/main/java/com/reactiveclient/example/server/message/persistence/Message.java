package com.reactiveclient.example.server.message.persistence;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@Entity
public class Message {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "content")
    private String content;
}
