package com.example.exchangeinfoapi;

import com.example.exchangeinfoapi.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class CurrencyServiceTest {

    @Autowired
    CurrencyService currencyService;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @Test
    void saveAllAndGetAll() {
        currencyService.saveAll(TestData.CURRENCIES);
        assertThat(currencyService.getAll()).containsAll(TestData.CURRENCIES).hasSize(3);
    }

    @Test
    void get() {
        currencyService.saveAll(TestData.CURRENCIES);
        assertThat(currencyService.get(TestData.CURRENCY_2.getName()).get()).isEqualTo(TestData.CURRENCY_2);
    }
}
