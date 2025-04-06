package com.nbicocchi.composite.service;

import com.nbicocchi.composite.persistence.model.DateInfos;
import com.nbicocchi.composite.persistence.repository.DateInfosRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Log4j2
@Component
public class DBIntegration {
    DateInfosRepository dateInfosRepository;

    public DBIntegration(DateInfosRepository dateInfosRepository) {
        this.dateInfosRepository = dateInfosRepository;
    }

    @Cacheable(cacheNames = "infos")
    public String getInfosWithCache(int month, int day) {
        Optional<DateInfos> infos = dateInfosRepository.findDateInfosByMonthAndDay(month, day);
        return infos.map(DateInfos::getInfo).orElse("n/a");
    }
}
