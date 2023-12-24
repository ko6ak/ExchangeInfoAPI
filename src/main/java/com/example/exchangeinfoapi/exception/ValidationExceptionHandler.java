package com.example.exchangeinfoapi.exception;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.*;

@ControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> notValidCurrency(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        List<ConstraintViolation<?>> list = new ArrayList<>(ex.getConstraintViolations());

        list.forEach(cv -> errors.put(cv.getPropertyPath().toString().split("\\.")[1],
                cv.getInvalidValue() + " - " + cv.getMessage()));

        return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> notValidAmount(MethodArgumentTypeMismatchException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getPropertyName(), ex.getMessage());
        return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> notValidAmount(MissingServletRequestParameterException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("amount", ex.getMessage());
        return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).body(errors);
    }
}
