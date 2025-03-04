package com.nbicocchi.building_spring_application;

import com.nbicocchi.building_spring_application.model.UserModel;
import com.nbicocchi.building_spring_application.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App implements ApplicationRunner {
	UserRepository userRepository;

	public App(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Override
	public void run(ApplicationArguments args) {
		userRepository.save(new UserModel("a@abc.it", "Lucio", "IT"));
		userRepository.save(new UserModel("b@abc.it", "Francois", "FR"));
		userRepository.save(new UserModel("c@abc.it", "John", "UK"));
	}
}
