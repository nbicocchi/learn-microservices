package com.baeldung.ls.persistence.repository;

import java.util.Collection;
import java.util.Optional;

import com.baeldung.ls.persistence.model.Project;

public interface IProjectRepository {

    Optional<Project> findById(Long id);

    Collection<Project> findAll();

    Project save(Project project);
}
