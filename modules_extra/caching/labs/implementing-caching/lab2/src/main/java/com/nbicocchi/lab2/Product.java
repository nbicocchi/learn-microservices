package com.caching.lab2;

import java.io.Serial;
import java.io.Serializable;

public record Product(int id, String name) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

}
