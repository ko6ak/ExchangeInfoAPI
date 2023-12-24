package com.example.exchangeinfoapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ExchangerateErrorResponseDTO {

    boolean success;
    Map<String, Object> error;
}
