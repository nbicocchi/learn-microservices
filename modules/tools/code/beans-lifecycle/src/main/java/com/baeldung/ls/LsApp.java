package com.baeldung.ls;

import com.baeldung.ls.persistence.model.Project;
import com.baeldung.ls.persistence.repository.IProjectRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDate;
import java.util.Random;
import java.util.random.RandomGenerator;

@SpringBootApplication
public class LsApp {
    public static final RandomGenerator RND = RandomGenerator.getDefault();

    IProjectRepository projectRepository;

    public LsApp(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public static void main(final String... args) {
        ConfigurableApplicationContext context = SpringApplication.run(LsApp.class, args);
        //context.close();
    }
}
