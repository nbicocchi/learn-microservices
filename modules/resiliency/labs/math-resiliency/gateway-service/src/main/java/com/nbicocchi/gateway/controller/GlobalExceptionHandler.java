package com.nbicocchi.gateway.controller;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleHttpServerError(HttpServerErrorException ex, WebRequest request) {
        log.error("Errore 500 da microservizio: {} | Path: {}", ex.getMessage(), request.getDescription(false));
    }

    @ExceptionHandler(HttpClientErrorException.TooManyRequests.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public void handleTooManyRequests(HttpClientErrorException.TooManyRequests ex, WebRequest request) {
        log.warn("Troppe richieste (429): {} | Path: {}", ex.getMessage(), request.getDescription(false));
    }

    @ExceptionHandler(BulkheadFullException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public void handleBulkheadFull(BulkheadFullException ex, WebRequest request) {
        log.warn("Bulkhead pieno: {} | Path: {}", ex.getMessage(), request.getDescription(false));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public void handleGeneric(Exception ex, WebRequest request) {
        log.error("Errore generico: {} | Path: {}", ex.getMessage(), request.getDescription(false));
    }
}
