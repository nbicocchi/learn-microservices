package com.nbicocchi.subscriber.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecificEvent {
    String id;
    Double amount;
}
