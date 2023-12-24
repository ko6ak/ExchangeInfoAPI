package com.example.exchangeinfoapi.controller;

import com.example.exchangeinfoapi.dto.CurrencyResponseDTO;
import com.example.exchangeinfoapi.dto.ExchangerateResponseDTO;
import com.example.exchangeinfoapi.dto.ExchangerateErrorResponseDTO;
import com.example.exchangeinfoapi.dto.MessageResponseDTO;
import com.example.exchangeinfoapi.entity.Currency;
import com.example.exchangeinfoapi.exception.CurrencyNotFoundException;
import com.example.exchangeinfoapi.exception.ExchangerateException;
import com.example.exchangeinfoapi.service.CurrencyService;
import com.example.exchangeinfoapi.validation.CheckCurrency;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Validated
@RestController
@RequiredArgsConstructor
public class CurrencyController {
    public static final String CURRENCY_SOURCE = "USD";
    public static final String URL = "http://api.exchangerate.host/live?access_key={key}";
    public static final String CURRENCY_NOT_FOUND = "This currency not found";

    @Value("${exchange_info_api.access_key}")
    private String key;

    private final CurrencyService currencyService;

    private final RestTemplate restTemplate;

    @GetMapping("/get-quotes")
    public ResponseEntity<?> getQuotes(@CheckCurrency @RequestParam("source") String source){

        List<Currency> currencies = currencyService.getAll();

        if (currencies.isEmpty() ||
                currencies.get(0).getTimestamp().compareTo(Timestamp.from(Instant.now().minus(1, ChronoUnit.HOURS))) < 0) {
            try {
                updateQuotes();
            }
            catch (ExchangerateException e) {
                return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body(new MessageResponseDTO(e.getMessage()));
            }
            currencies = currencyService.getAll();
        }

        Map<String, BigDecimal> result = new TreeMap<>();

        if (CURRENCY_SOURCE.equals(source)) {
            currencies.forEach(c -> result.put(c.getName(), c.getQuote()));
            return ResponseEntity.ok(result);
        }

        Currency currency;
        try{
            currency = currencies.stream()
                    .filter(c -> c.getName().substring(3).equals(source))
                    .findFirst()
                    .orElseThrow(() -> new CurrencyNotFoundException(CURRENCY_NOT_FOUND));
        }
        catch (CurrencyNotFoundException e) {
            return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).body(new MessageResponseDTO(e.getMessage()));
        }

        BigDecimal sourceQuote = currency.getQuote();

        currencies.stream().filter(c -> !c.getName().substring(3).equals(source))
                .forEach(c -> result.put(source + c.getName().substring(3),
                        c.getQuote().divide(sourceQuote, 6, RoundingMode.DOWN)));
        result.put(source + CURRENCY_SOURCE, BigDecimal.ONE.divide(sourceQuote, 6, RoundingMode.DOWN));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/convert")
    public ResponseEntity<?> convert(@CheckCurrency @RequestParam("from") String from,
                                     @CheckCurrency @RequestParam("to") String to,
                                     @RequestParam("amount") @DecimalMin(value = "0.0", inclusive = false)
                                         @Digits(integer=20, fraction=2) BigDecimal amount){

        List<Currency> currencies = currencyService.getAll();

        if (currencies.isEmpty() ||
                currencies.get(0).getTimestamp().compareTo(Timestamp.from(Instant.now().minus(1, ChronoUnit.HOURS))) < 0) {
            try {
                updateQuotes();
            }
            catch (ExchangerateException e) {
                return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body(new MessageResponseDTO(e.getMessage()));
            }
        }

        BigDecimal fromQuote, toQuote;
        try{
            if (CURRENCY_SOURCE.equals(from)) return ResponseEntity.ok(
                    new CurrencyResponseDTO(
                            currencyService.get(from + to).orElseThrow(() -> new CurrencyNotFoundException(CURRENCY_NOT_FOUND))
                                    .getQuote().multiply(amount).toString()));

            fromQuote = currencyService.get(CURRENCY_SOURCE + from)
                    .orElseThrow(() -> new CurrencyNotFoundException(CURRENCY_NOT_FOUND)).getQuote();
            toQuote = currencyService.get(CURRENCY_SOURCE + to)
                    .orElseThrow(() -> new CurrencyNotFoundException(CURRENCY_NOT_FOUND)).getQuote();
        }
        catch (CurrencyNotFoundException e) {
            return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).body(new MessageResponseDTO(e.getMessage()));
        }

        return ResponseEntity.ok(
                new CurrencyResponseDTO(toQuote.divide(fromQuote, 10, RoundingMode.DOWN)
                        .multiply(amount).setScale(6, RoundingMode.DOWN).toString()));
    }

    public void updateQuotes() {
        ResponseEntity<ExchangerateResponseDTO> response = restTemplate.getForEntity(URL, ExchangerateResponseDTO.class, key);

        if (!Objects.requireNonNull(response.getBody()).isSuccess()) {
            ResponseEntity<ExchangerateErrorResponseDTO> errorResponse = restTemplate.getForEntity(URL, ExchangerateErrorResponseDTO.class, key);
            Map<String, Object> errors = Objects.requireNonNull(errorResponse.getBody()).getError();
            StringBuilder stringBuilder = new StringBuilder();

            errors.forEach((k, v) -> stringBuilder.append(k).append(": ").append(v).append("; "));
            throw new ExchangerateException(stringBuilder.toString());
        }

        ExchangerateResponseDTO responseDTO = response.getBody();

        if (responseDTO.isSuccess()) {
            List<Currency> currencyList = new ArrayList<>();

            responseDTO.getQuotes().forEach((currencyPair, quote) -> {
                currencyList.add(new Currency(
                        currencyPair,
                        quote,
                        Timestamp.from(Instant.ofEpochSecond(responseDTO.getTimestamp()))));
            });
            currencyService.saveAll(currencyList);
        }
    }
}
