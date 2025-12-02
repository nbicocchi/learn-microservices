package com.nbicocchi.publisher.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecificEvent {
    String id;
    String action;
    Double amount;
}
