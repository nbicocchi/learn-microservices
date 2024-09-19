package com.baeldung.composite;

import java.time.LocalTime;

public record LocalDateTimeWithTimestamp(LocalDateWithTimestamp localDateWithTimestamp, LocalTimeWithTimestamp localTimeWithTimestamp) {
}
