package com.baeldung.lsd;

import com.baeldung.lsd.persistence.model.Project;
import com.baeldung.lsd.persistence.repository.IProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import reactor.core.publisher.Flux;

@SpringBootApplication
@EnableMongoRepositories
public class LsApp implements CommandLineRunner{
	private static final Logger log = LoggerFactory.getLogger(LsApp.class);
	IProjectRepository IProjectRepository;

	public LsApp(IProjectRepository IProjectRepository) {
		this.IProjectRepository = IProjectRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(LsApp.class, args);
	}
	
	public void run(String... args) {
		IProjectRepository.deleteAll().block();

		IProjectRepository.save(new Project("P01", "Project 01", "About Project 01")).block();
		IProjectRepository.save(new Project("P02", "Project 02", "About Project 02")).block();
		IProjectRepository.save(new Project("P03", "Project 03", "About Project 03")).block();

		Flux<Project> projects = IProjectRepository.findAll();
		projects.subscribe(System.out::println);
	}
}

