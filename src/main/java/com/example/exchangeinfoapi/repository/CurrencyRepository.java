package com.example.exchangeinfoapi.repository;

import com.example.exchangeinfoapi.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyRepository  extends JpaRepository<Currency, String> {
    Optional<Currency> findByName(String name);
}
