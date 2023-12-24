package com.example.exchangeinfoapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ExchangeInfoApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExchangeInfoApiApplication.class, args);
    }
}
