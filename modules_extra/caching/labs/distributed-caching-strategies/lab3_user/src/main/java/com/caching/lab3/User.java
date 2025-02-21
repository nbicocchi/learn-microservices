package com.caching.lab3;

import java.io.Serial;
import java.io.Serializable;

public record User(int id, String name) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

}

