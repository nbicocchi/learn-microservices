package com.valentini.userservice.exception;


public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}

