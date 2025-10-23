package com.baeldung.raft.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Global exception handler for the Raft Implementation application.
 * <p>
 * This class uses {@code @ControllerAdvice} to intercept and handle exceptions thrown by
 * controller methods across the application. It provides methods to handle specific exceptions
 * and return meaningful HTTP responses to the client.
 * </p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link NodeStateNotFoundException} thrown by any controller method.
     *
     * @param ex the {@code NodeStateNotFoundException} that was thrown
     * @return a {@code Mono} emitting a {@code ResponseEntity} with a 404 NOT FOUND status and the exception message
     */
    @ExceptionHandler(NodeStateNotFoundException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Node state not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<String>> handleNodeStateNotFoundException(NodeStateNotFoundException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()));
    }

    /**
     * Handles {@link IllegalArgumentException} thrown by any controller method.
     *
     * @param ex the {@code IllegalArgumentException} that was thrown
     * @return a {@code Mono} emitting a {@code ResponseEntity} with a 400 BAD REQUEST status and the exception message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()));
    }
}
