package com.nbicocchi.user;

import com.nbicocchi.user.model.UserModel;
import com.nbicocchi.user.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Iterator;
import java.util.List;

@Log4j2
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
	public void run(ApplicationArguments args) throws Exception {
		// for debug purposes, do not use in production!
		userRepository.deleteAll();
		userRepository.save(new UserModel("John Doe", "john@doe.com"));
		userRepository.save(new UserModel("Mario Rossi", "mario@doe.com"));
		userRepository.save(new UserModel("Cecilia Verdi", "cecilia@doe.com"));

		Iterable<UserModel> users = userRepository.findAll();
		for (UserModel user : users) {
			log.info(user);
		}
	}
}
