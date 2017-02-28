package com.reactiveclient.example.server.message;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class MessageResource implements Serializable {
    private Integer id;
    private String content;
}