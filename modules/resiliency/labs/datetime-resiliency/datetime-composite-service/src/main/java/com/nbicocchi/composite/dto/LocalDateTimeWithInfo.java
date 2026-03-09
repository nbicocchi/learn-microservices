package com.nbicocchi.composite.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record LocalDateTimeWithInfo(LocalDate localDate, LocalTime localTime, String info) {
}
