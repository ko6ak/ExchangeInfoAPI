package com.example.exchangeinfoapi.exception;

public class ExchangerateException extends RuntimeException{
    public ExchangerateException(String error) {
        super(error);
    }
}
