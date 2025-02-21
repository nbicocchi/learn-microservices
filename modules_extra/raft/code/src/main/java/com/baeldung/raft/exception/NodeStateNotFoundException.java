package com.baeldung.raft.exception;

/**
 * Exception thrown when the state of a node in the Raft cluster cannot be found.
 * <p>
 * This exception is typically used to indicate that a requested node's state does not exist
 * in the persistence layer or has not been initialized.
 * </p>
 */
public class NodeStateNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code NodeStateNotFoundException} with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public NodeStateNotFoundException(String message) {
        super(message);
    }
}
