package com.nbicocchi.composite.controller;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@Log4j2
@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(BulkheadFullException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public void bulkheadException() {
        log.warn("bulkheadException()");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(HttpServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void httpServerErrorException() {
        log.error("httpServerErrorException()");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(HttpClientErrorException.TooManyRequests.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public void tooManyRequests() {
        log.warn("tooManyRequests()");
    }
}