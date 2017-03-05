package com.reactiveclient.core.example;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Beer implements Serializable {
    private String code;
    private String name;
    private String description;
    private BigDecimal abv;
    private BigDecimal stars;
}