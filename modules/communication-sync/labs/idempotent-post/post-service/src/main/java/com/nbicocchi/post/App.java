package com.nbicocchi.post;

import com.nbicocchi.post.persistence.model.Post;
import com.nbicocchi.post.persistence.repository.PostRepository;
import lombok.extern.java.Log;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;

@Log
@SpringBootApplication
public class App implements ApplicationRunner {
    PostRepository postRepository;

    public App(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public static void main(final String... args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        postRepository.save(new Post("171f5df0-b213-4a40-8ae6-fe82239ab660", LocalDateTime.of(2025,3,1, 10, 30), "hello!"));
        postRepository.save(new Post("171f5df0-b213-4a40-8ae6-fe82239ab660", LocalDateTime.of(2025,3,1, 10, 32), "i'm json!"));
        postRepository.save(new Post("b1f4748a-f3cd-4fc3-be58-38316afe1574", LocalDateTime.of(2025,3,1, 10, 32), "looking for an apartment"));

        Iterable<Post> products = postRepository.findAll();
        for (Post product : products) {
            log.info(product.toString());
        }
    }
}
