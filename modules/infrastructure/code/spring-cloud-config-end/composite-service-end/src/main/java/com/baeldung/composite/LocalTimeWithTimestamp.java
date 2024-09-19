package com.baeldung.composite;

import java.time.LocalTime;

public record LocalTimeWithTimestamp(LocalTime localTime, LocalTime timestamp) {
}
