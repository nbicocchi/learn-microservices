package com.nbicocchi.user;

import com.nbicocchi.user.persistence.model.UserModel;
import com.nbicocchi.user.persistence.repository.UserRepository;
import lombok.extern.java.Log;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

@Log
@SpringBootApplication
public class App implements ApplicationRunner {
    UserRepository userRepository;

    public App(UserRepository postRepository) {
        this.userRepository = postRepository;
    }

    public static void main(final String... args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        userRepository.save(new UserModel("171f5df0-b213-4a40-8ae6-fe82239ab660", "hannibal", LocalDate.of(2000,3,1)));
        userRepository.save(new UserModel("b1f4748a-f3cd-4fc3-be58-38316afe1574", "shy_guy", LocalDate.of(2002,6,1)));

        Iterable<UserModel> users = userRepository.findAll();
        for (UserModel user : users) {
            log.info(user.toString());
        }
    }
}
