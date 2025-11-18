package com.nbicocchi.payment.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BankAccountEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateId; // e.g., orderId

    private String type;        // e.g., "order.created", "payment.valid"

    @Column(columnDefinition = "TEXT")
    private String payload;     // JSON serialized Event.data

    private ZonedDateTime createdAt;
}
