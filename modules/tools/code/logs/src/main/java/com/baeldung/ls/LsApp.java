package com.baeldung.ls;

import com.baeldung.ls.persistence.model.Project;
import com.baeldung.ls.persistence.repository.IProjectRepository;
import com.baeldung.ls.service.IProjectService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.Random;
import java.util.random.RandomGenerator;

@SpringBootApplication
public class LsApp implements ApplicationRunner {
    IProjectService projectService;

    public LsApp(IProjectService projectService) {
        this.projectService = projectService;
    }

    public static void main(final String... args) {
        SpringApplication.run(LsApp.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        projectService.save(new Project("P1", LocalDate.now()));
        projectService.save(new Project("P2", LocalDate.now()));
        projectService.save(new Project("P3", LocalDate.now()));
    }
}
