package com.luca.core.product.web.exceptions;

public class InvalidInputException extends RuntimeException {

  @Override
  public Throwable fillInStackTrace() {
    return this;
  }

  public InvalidInputException() {}

  public InvalidInputException(String message) {
    super(message);
  }

  public InvalidInputException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidInputException(Throwable cause) {
    super(cause);
  }
}
