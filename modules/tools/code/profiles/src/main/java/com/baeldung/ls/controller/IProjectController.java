package com.baeldung.ls.controller;

import com.baeldung.ls.persistence.model.Project;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RequestMapping(value = "/projects")
public interface IProjectController {
    @GetMapping(value = "/{id}")
    Project findOne(@PathVariable Long id);

    @GetMapping
    Collection<Project> findAll();

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Project create(@RequestBody Project project);
}
