package com.yash.banking_transaction_service.exceptions;

import com.yash.banking_transaction_service.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, List<String>> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.computeIfAbsent(
                                error.getField(),
                                key -> new ArrayList<>()
                        ).add(error.getDefaultMessage())
                );

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors,
                Instant.now().toString()
        );

        return ResponseEntity
                .badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(AccountNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), Collections.emptyMap(), Instant.now().toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(AccountStateException.class)
    public ResponseEntity<ErrorResponse> handleAccountStateException(AccountStateException ex){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(),ex.getMessage(),Collections.emptyMap(),Instant.now().toString());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}
