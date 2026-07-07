package com.nbicocchi.comment;

import com.nbicocchi.comment.persistence.model.Comment;
import com.nbicocchi.comment.persistence.repository.CommentRepository;
import lombok.extern.java.Log;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.UUID;

@Log
@SpringBootApplication
public class App implements ApplicationRunner {
    CommentRepository commentRepository;

    public App(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public static void main(final String... args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        commentRepository.save(new Comment(UUID.randomUUID().toString(), "b1f4748a-f3cd-4fc3-be58-38316afe1574", LocalDateTime.of(2025,3,1, 10, 33), "hme too!"));
        commentRepository.save(new Comment(UUID.randomUUID().toString(), "b1f4748a-f3cd-4fc3-be58-38316afe1574", LocalDateTime.of(2025,3,1, 10, 34), "cannot find anything too!"));
        commentRepository.save(new Comment(UUID.randomUUID().toString(), "b1f4748a-f3cd-4fc3-be58-38316afe1574", LocalDateTime.of(2025,3,1, 10, 35), "call me! 356-4554-345"));

        Iterable<Comment> comments = commentRepository.findAll();
        for (Comment comment : comments) {
            log.info(comment.toString());
        }
    }
}
