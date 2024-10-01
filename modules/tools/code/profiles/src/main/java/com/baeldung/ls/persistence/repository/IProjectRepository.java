package com.baeldung.ls.persistence.repository;

import com.baeldung.ls.persistence.model.Project;

import java.util.Collection;
import java.util.Optional;

public interface IProjectRepository {

    Optional<Project> findById(Long id);

    Collection<Project> findAll();

    Project save(Project project);
}
