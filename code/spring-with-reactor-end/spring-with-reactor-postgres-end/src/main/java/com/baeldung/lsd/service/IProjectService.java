package com.baeldung.lsd.service;

import com.baeldung.lsd.persistence.model.Project;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface IProjectService {
    Mono<Project> findById(String id);

    Flux<Project> findAll();

    Mono<Project> save(Project project);

    void deleteById(String id);
}
