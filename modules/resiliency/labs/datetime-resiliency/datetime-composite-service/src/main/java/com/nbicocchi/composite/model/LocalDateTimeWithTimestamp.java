package com.nbicocchi.composite.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record LocalDateTimeWithTimestamp(
        LocalDate localDate, LocalTime localTime, LocalDateTime timestamp) {
}
