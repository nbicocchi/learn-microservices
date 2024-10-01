package com.baeldung.ls.persistence.repository.impl;

import com.baeldung.ls.persistence.model.Project;
import com.baeldung.ls.persistence.repository.IProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ProjectRepositoryImpl implements IProjectRepository {
    public static final Logger LOG = LoggerFactory.getLogger(ProjectRepositoryImpl.class);

    @Value("${project.prefix}")
    private String prefix;

    @Value("${project.suffix}")
    private Integer suffix;

    @Value("${additional.info}")
    private String additional;

    private final List<Project> projects = new ArrayList<>();

    @Override
    public Optional<Project> findById(Long id) {
        return projects.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    @Override
    public Collection<Project> findAll() {
        return projects;
    }

    @Override
    public Project save(Project project) {
        Project toSave = new Project(project);
        if (Objects.isNull(toSave.getId())) {
            toSave.setId(new Random().nextLong(1_000_000L));
        }
        Optional<Project> existingProject = findById(project.getId());
        if (existingProject.isPresent()) {
            projects.remove(existingProject);
        }
        updateInternalId(toSave);
        projects.add(toSave);
        return toSave;
    }

    private void updateInternalId(Project project) {
        LOG.info("Additional Info " + additional);
        LOG.info("Prepending Prefix " + prefix);
        LOG.info("Appending Suffix " + suffix);

        project.setInternalID(prefix + "-" + project.getId() + "-" + suffix);

        LOG.info("Generated internal id " + project.getInternalID());
    }
}
