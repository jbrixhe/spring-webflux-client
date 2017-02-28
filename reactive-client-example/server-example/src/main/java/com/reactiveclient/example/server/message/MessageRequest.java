package com.reactiveclient.example.server.message;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class MessageRequest implements Serializable {
    @NotNull
    private String content;
}