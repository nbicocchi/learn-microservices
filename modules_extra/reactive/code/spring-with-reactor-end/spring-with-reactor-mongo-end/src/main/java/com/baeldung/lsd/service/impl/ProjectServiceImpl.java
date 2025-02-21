package com.baeldung.lsd.service.impl;

import com.baeldung.lsd.persistence.model.Project;
import com.baeldung.lsd.persistence.repository.IProjectRepository;
import com.baeldung.lsd.service.IProjectService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProjectServiceImpl implements IProjectService {

    private final IProjectRepository IProjectRepository;

    public ProjectServiceImpl(IProjectRepository IProjectRepository) {
        this.IProjectRepository = IProjectRepository;
    }

    @Override
    public Mono<Project> findById(String id) {
        return IProjectRepository.findById(id);
    }

    @Override
    public Flux<Project> findAll() {
        return IProjectRepository.findAll();
    }

    @Override
    public Mono<Project> save(Project project) {
        return IProjectRepository.save(project);
    }

    @Override
    public void deleteById(String id) {
        IProjectRepository.deleteById(id);
    }
}
