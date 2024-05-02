package com.baeldung.lsd;

import com.baeldung.lsd.persistence.model.Project;
import com.baeldung.lsd.persistence.repository.IProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
public class LsApp implements CommandLineRunner{
	private static final Logger LOG = LoggerFactory.getLogger(LsApp.class);
	IProjectRepository IProjectRepository;

	@Bean
	public Scheduler jdbcScheduler() {
		int threadPoolSize = 10;
		int taskQueueSize = 100;
		LOG.info("Creates a jdbcScheduler with thread pool size = {}", threadPoolSize);
		return Schedulers.newBoundedElastic(threadPoolSize, taskQueueSize, "jdbc-pool");
	}

	public LsApp(IProjectRepository IProjectRepository) {
		this.IProjectRepository = IProjectRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(LsApp.class, args);
	}
	
	public void run(String... args) {
		IProjectRepository.deleteAll();

		IProjectRepository.save(new Project("P01", "Project 01", "About Project 01"));
		IProjectRepository.save(new Project("P02", "Project 02", "About Project 02"));
		IProjectRepository.save(new Project("P03", "Project 03", "About Project 03"));

		Iterable<Project> projects = IProjectRepository.findAll();
		for (Project project : projects) {
			System.out.println(project);
		}
	}
}

