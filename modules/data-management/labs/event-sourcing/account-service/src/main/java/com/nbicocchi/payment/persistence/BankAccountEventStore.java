package com.nbicocchi.payment.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbicocchi.payment.events.Event;
import com.nbicocchi.payment.persistence.model.BankAccountEvent;
import com.nbicocchi.payment.persistence.repository.BankAccountEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankAccountEventStore {
    private final BankAccountEventRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();

    public void append(String aggregateId, Event<String, ?> event) {
        try {
            BankAccountEvent entity = new BankAccountEvent();
            entity.setAggregateId(aggregateId);
            entity.setType(event.getKey());
            entity.setPayload(mapper.writeValueAsString(event.getData()));
            entity.setCreatedAt(event.getEventCreatedAt());
            repository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<Event<String, T>> load(String aggregateId, Class<T> clazz) {
        return repository.findByAggregateIdOrderByCreatedAtAsc(aggregateId).stream().map(e -> {
            try {
                T data = mapper.readValue(e.getPayload(), clazz);
                return new Event<>(e.getType(), data, e.getCreatedAt());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }).collect(Collectors.toList());
    }
}
