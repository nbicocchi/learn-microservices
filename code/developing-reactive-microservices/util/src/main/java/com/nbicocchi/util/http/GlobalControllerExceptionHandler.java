package com.nbicocchi.util.http;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import com.nbicocchi.api.exceptions.BadRequestException;
import com.nbicocchi.api.exceptions.InvalidInputException;
import com.nbicocchi.api.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
class GlobalControllerExceptionHandler {

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(BadRequestException.class)
  public @ResponseBody HttpErrorInfo handleBadRequestExceptions(
    ServerHttpRequest request, BadRequestException ex) {

    return new HttpErrorInfo(BAD_REQUEST,
            request.getPath().pathWithinApplication().value(),
            ex.getMessage());
  }

  @ResponseStatus(NOT_FOUND)
  @ExceptionHandler(NotFoundException.class)
  public @ResponseBody HttpErrorInfo handleNotFoundExceptions(
    ServerHttpRequest request, NotFoundException ex) {

    return new HttpErrorInfo(NOT_FOUND,
            request.getPath().pathWithinApplication().value(),
            ex.getMessage());
  }

  @ResponseStatus(UNPROCESSABLE_ENTITY)
  @ExceptionHandler(InvalidInputException.class)
  public @ResponseBody HttpErrorInfo handleInvalidInputException(
    ServerHttpRequest request, InvalidInputException ex) {

    return new HttpErrorInfo(UNPROCESSABLE_ENTITY,
            request.getPath().pathWithinApplication().value(),
            ex.getMessage());
  }
}
