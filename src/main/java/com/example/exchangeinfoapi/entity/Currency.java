package com.example.exchangeinfoapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Currency {

    @Id
    private String name;

    @Column(precision = 20, scale = 6)
    private BigDecimal quote;

    private Timestamp timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return Objects.equals(name, currency.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
