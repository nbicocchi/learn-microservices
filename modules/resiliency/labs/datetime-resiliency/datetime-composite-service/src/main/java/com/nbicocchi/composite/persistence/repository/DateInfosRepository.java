package com.nbicocchi.composite.persistence.repository;

import com.nbicocchi.composite.persistence.model.DateInfos;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DateInfosRepository extends CrudRepository<DateInfos, Long> {
    Optional<DateInfos> findDateInfosByMonthAndDay(int month, int day);
}