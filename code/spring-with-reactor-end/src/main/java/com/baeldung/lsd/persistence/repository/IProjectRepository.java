package com.baeldung.lsd.persistence.repository;

import com.baeldung.lsd.persistence.model.Project;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface IProjectRepository extends ReactiveMongoRepository<Project, String> {
    Mono<Project> findByCode(String code);

}
