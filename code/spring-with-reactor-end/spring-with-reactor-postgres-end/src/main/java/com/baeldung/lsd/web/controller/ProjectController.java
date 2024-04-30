package com.baeldung.lsd.web.controller;

import com.baeldung.lsd.persistence.model.Project;
import com.baeldung.lsd.service.IProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/projects")
public class ProjectController {

    private final IProjectService projectService;

    public ProjectController(IProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping(value = "/{id}")
    public Mono<Project> findOne(@PathVariable String id) {
        return projectService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Project> create(@RequestBody Project newProject) {
        return projectService.save(newProject);
    }

    @GetMapping
    public Flux<Project> findAll() {
        return projectService.findAll();
    }

    @PutMapping("/{id}")
    public Mono<Project> updateProject(@PathVariable("id") String id, @RequestBody Project updatedProject) {
        return projectService.save(updatedProject);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable("id") String id) {
        projectService.deleteById(id);
    }
}