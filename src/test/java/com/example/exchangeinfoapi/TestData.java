package com.example.exchangeinfoapi;

import com.example.exchangeinfoapi.entity.Currency;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

public class TestData {

    public static final Currency CURRENCY_1 = new Currency("USDRUB", new BigDecimal("92.000000"),
            Timestamp.valueOf(LocalDateTime.of(2023, Month.DECEMBER, 23, 15, 0, 0)));
    public static final Currency CURRENCY_2 = new Currency("USDGBP", new BigDecimal("0.780000"),
            Timestamp.valueOf(LocalDateTime.of(2023, Month.DECEMBER, 23, 15, 0, 0)));
    public static final Currency CURRENCY_3 = new Currency("USDCHF", new BigDecimal("0.850000"),
            Timestamp.valueOf(LocalDateTime.of(2023, Month.DECEMBER, 23, 15, 0, 0)));

    public static final Currency CURRENCY_4 = new Currency("USDRUB", new BigDecimal("92.000000"),
            Timestamp.valueOf(LocalDateTime.now()));
    public static final Currency CURRENCY_5 = new Currency("USDGBP", new BigDecimal("0.780000"),
            Timestamp.valueOf(LocalDateTime.now()));
    public static final Currency CURRENCY_6 = new Currency("USDCHF", new BigDecimal("0.850000"),
            Timestamp.valueOf(LocalDateTime.now()));

    public static final String QUOTES = "{\"GBPCHF\":1.089743,\"GBPRUB\":117.948717,\"GBPUSD\":1.282051}";

    public static final List<Currency> CURRENCIES = new ArrayList<>();
    public static final List<Currency> CURRENCIES_1 = new ArrayList<>();

    static {
        Collections.addAll(CURRENCIES, CURRENCY_1, CURRENCY_2, CURRENCY_3);
        Collections.addAll(CURRENCIES_1, CURRENCY_4, CURRENCY_5, CURRENCY_6);
    }
}
