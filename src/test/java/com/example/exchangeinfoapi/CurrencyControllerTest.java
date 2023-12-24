package com.example.exchangeinfoapi;

import com.example.exchangeinfoapi.controller.CurrencyController;
import com.example.exchangeinfoapi.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@ExtendWith(MockitoExtension.class)
public class CurrencyControllerTest {

    @Mock
    private CurrencyService currencyService;

    @Mock
    private RestTemplate restTemplate;

    private MockMvc mvc;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new CurrencyController(currencyService, restTemplate)).build();
    }

    @Test
    void getQuotesOk() throws Exception {
        when(currencyService.getAll()).thenReturn(TestData.CURRENCIES_1);

        AtomicReference<String> response = new AtomicReference<>();

        mvc.perform(get("/get-quotes").param("source", "GBP"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> response.set(result.getResponse().getContentAsString(StandardCharsets.UTF_8)));

        assertThat(response.get()).isEqualTo(TestData.QUOTES);
    }

    @Test
    void getQuotesNotFound() throws Exception {
        when(currencyService.getAll()).thenReturn(TestData.CURRENCIES_1);

        mvc.perform(get("/get-quotes").param("source", "ABC"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(CurrencyController.CURRENCY_NOT_FOUND));
    }

    @Test
    void convertOk() throws Exception {
        when(currencyService.getAll()).thenReturn(TestData.CURRENCIES_1);
        when(currencyService.get("USDRUB")).thenReturn(Optional.of(TestData.CURRENCY_1));
        when(currencyService.get("USDGBP")).thenReturn(Optional.of(TestData.CURRENCY_2));

        mvc.perform(get("/convert")
                        .param("from", "RUB")
                        .param("to", "GBP")
                        .param("amount", "1000"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").value("8.478260"));
    }

    @Test
    void convertNotFound() throws Exception {
        when(currencyService.getAll()).thenReturn(TestData.CURRENCIES_1);

        mvc.perform(get("/convert")
                        .param("from", "RUR")
                        .param("to", "GBP")
                        .param("amount", "1000"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(CurrencyController.CURRENCY_NOT_FOUND));
    }
}
