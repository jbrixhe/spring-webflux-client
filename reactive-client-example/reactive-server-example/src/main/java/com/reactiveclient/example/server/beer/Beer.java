package com.reactiveclient.example.server.beer;

import lombok.*;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Beer implements Serializable {
    @NotNull
    @Max(value = 128)
    private String code;
    @NotNull
    @Max(value = 128)
    private String name;
    @NotNull
    @Max(value = 512)
    private String description;
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal abv;
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    private BigDecimal stars;
}