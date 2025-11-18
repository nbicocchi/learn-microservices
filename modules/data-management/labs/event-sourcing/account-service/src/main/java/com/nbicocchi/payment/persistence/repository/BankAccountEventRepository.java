package com.nbicocchi.payment.persistence.repository;

import com.nbicocchi.payment.persistence.model.BankAccountEvent;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BankAccountEventRepository extends CrudRepository<BankAccountEvent, Long> {
    List<BankAccountEvent> findByAggregateIdOrderByCreatedAtAsc(String aggregateId);
}