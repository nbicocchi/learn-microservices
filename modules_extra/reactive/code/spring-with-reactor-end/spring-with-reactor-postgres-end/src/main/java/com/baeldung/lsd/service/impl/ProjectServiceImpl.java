package com.baeldung.lsd.service.impl;

import com.baeldung.lsd.persistence.model.Project;
import com.baeldung.lsd.persistence.repository.IProjectRepository;
import com.baeldung.lsd.service.IProjectService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Service
public class ProjectServiceImpl implements IProjectService {

    private final IProjectRepository IProjectRepository;
    private final Scheduler jdbcScheduler;

    public ProjectServiceImpl(IProjectRepository IProjectRepository, Scheduler jdbcScheduler) {
        this.IProjectRepository = IProjectRepository;
        this.jdbcScheduler = jdbcScheduler;
    }

    @Override
    public Mono<Project> findById(String id) {
        return Mono.fromCallable(() -> internalFindById(id))
                .subscribeOn(jdbcScheduler);
    }

    private Project internalFindById(String id) {
        return IProjectRepository.findById(id).get();
    }

    @Override
    public Flux<Project> findAll() {
        return Mono.fromCallable(() -> internalFindAll())
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(jdbcScheduler);
    }

    private Iterable<Project> internalFindAll() {
        return IProjectRepository.findAll();
    }

    @Override
    public Mono<Project> save(Project project) {
        return Mono.fromCallable(() -> internalSave(project))
                .subscribeOn(jdbcScheduler);
    }

    private Project internalSave(Project project) {
        return IProjectRepository.save(project);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return Mono.fromRunnable(() -> internalDeleteById(id))
                .subscribeOn(jdbcScheduler).then();
    }

    private void internalDeleteById(String id) {
        IProjectRepository.deleteById(id);
    }
}
