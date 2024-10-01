package com.baeldung.ls.service.impl;

import java.util.Collection;
import java.util.Optional;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baeldung.ls.persistence.model.Project;
import com.baeldung.ls.persistence.repository.IProjectRepository;
import com.baeldung.ls.service.IProjectService;

@Service
public class ProjectServiceImpl implements IProjectService {
    private IProjectRepository projectRepository;
    private IProjectRepository projectRepository2;

    public ProjectServiceImpl(IProjectRepository projectRepository, IProjectRepository projectRepository2) {
        this.projectRepository = projectRepository;
        this.projectRepository2 = projectRepository2;
    }

    @Override
    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    @Override
    public Collection<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @PostConstruct
    public void postConstruct() {
        // breakpoint here!
    }

}
