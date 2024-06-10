package com.luca.core.product.web.exceptions;

public class NotFoundException extends RuntimeException {

  @Override
  public Throwable fillInStackTrace() {
    return this;
  }

  public NotFoundException() {}

  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public NotFoundException(Throwable cause) {
    super(cause);
  }
}
