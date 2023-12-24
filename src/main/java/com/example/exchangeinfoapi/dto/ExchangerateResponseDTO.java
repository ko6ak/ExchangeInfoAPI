package com.example.exchangeinfoapi.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ExchangerateResponseDTO {

    boolean success;
    String terms;
    String privacy;
    long timestamp;
    String source;
    Map<String, BigDecimal> quotes;
}
