package com.baeldung.lsd.persistence.repository;

import com.baeldung.lsd.persistence.model.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ProjectRepositoryIntegrationTest {

    IProjectRepository repository;

    @Autowired
    public ProjectRepositoryIntegrationTest(IProjectRepository repository) {
        this.repository = repository;
    }

    @Test
    public void givenProject_whenFindByCode_thenFindProject() {
        repository.deleteAll().block();
        repository.save(new Project("P01", "Bill", "Bill is brave")).block();
        Mono<Project> projects = repository.findByCode("P01");

        StepVerifier.create(projects).assertNext(project -> {
            assertEquals("Bill", project.getName());
            assertNotNull(project.getId());
        }).expectComplete().verify();
    }

    @Test
    public void givenProject_whenSave_thenSaveProject() {
        repository.deleteAll().block();
        Mono<Project> projectMono = repository.save(new Project("P01", "Bill", "Bill is brave"));

        StepVerifier
                .create(projectMono)
                .assertNext(project -> assertNotNull(project.getId()))
                .expectComplete()
                .verify();
    }

    @Test
    public void givenProject_whenUpdate_thenUpdate() {
        repository.deleteAll().block();
        Mono<Project> projectMono = repository.save(new Project("P01", "Bill", "Bill is brave"));
        Project project = projectMono.block();
        project.setName("Bill updated!");
        Mono<Project> updatedProjectMono = repository.save(project);

        StepVerifier
                .create(updatedProjectMono)
                .assertNext(p -> assertEquals("Bill updated!", p.getName()))
                .expectComplete()
                .verify();
    }
}
