package com.nbicocchi.publisher.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record SpecificEvent(String id, String action, Double amount) {};

