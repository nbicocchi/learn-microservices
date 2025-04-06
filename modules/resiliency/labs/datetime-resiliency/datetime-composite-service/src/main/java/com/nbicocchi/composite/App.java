package com.nbicocchi.composite;

import com.nbicocchi.composite.persistence.model.DateInfos;
import com.nbicocchi.composite.persistence.repository.DateInfosRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;


@Log4j2
@SpringBootApplication
public class App implements ApplicationRunner {
    DateInfosRepository dateInfosRepository;

    public App(DateInfosRepository dateInfosRepository) {
        this.dateInfosRepository = dateInfosRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    @Override
    public void run(ApplicationArguments args) {
        Iterable<DateInfos> dateInfos = dateInfosRepository.findAll();
        for (DateInfos dateInfo : dateInfos) {
            log.info(dateInfo);
        }
    }
}
