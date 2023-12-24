package com.example.exchangeinfoapi.service;

import com.example.exchangeinfoapi.entity.Currency;
import com.example.exchangeinfoapi.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    public void saveAll(List<Currency> currencyList) {
        currencyRepository.saveAll(currencyList);
    }

    public List<Currency> getAll(){
        return currencyRepository.findAll();
    }

    public Optional<Currency> get(String name){
        return currencyRepository.findByName(name);
    }
}
