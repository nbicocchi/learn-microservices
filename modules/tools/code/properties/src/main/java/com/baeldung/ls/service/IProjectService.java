package com.baeldung.ls.service;

import java.util.Collection;
import java.util.Optional;

import com.baeldung.ls.persistence.model.Project;

public interface IProjectService {
    Optional<Project> findById(Long id);

    Collection<Project> findAll();

    Project save(Project project);
}
