package com.luca.core.product.web.exceptions;

public class BadRequestException extends RuntimeException {

  @Override
  public Throwable fillInStackTrace() {
    return this;
  }

  public BadRequestException() {}

  public BadRequestException(String message) {
    super(message);
  }

  public BadRequestException(String message, Throwable cause) {
    super(message, cause);
  }

  public BadRequestException(Throwable cause) {
    super(cause);
  }
}
