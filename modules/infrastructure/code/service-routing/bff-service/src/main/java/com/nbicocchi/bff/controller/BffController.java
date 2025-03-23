package com.nbicocchi.bff.controller;

import com.nbicocchi.bff.dto.*;
import com.nbicocchi.bff.integration.CommentIntegration;
import com.nbicocchi.bff.integration.PostIntegration;
import com.nbicocchi.bff.integration.UserIntegration;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("/bff")
public class BffController {
    UserIntegration userIntegration;
    PostIntegration postIntegration;
    CommentIntegration commentIntegration;

    public BffController(UserIntegration userIntegration, PostIntegration postIntegration, CommentIntegration commentIntegration) {
        this.userIntegration = userIntegration;
        this.postIntegration = postIntegration;
        this.commentIntegration = commentIntegration;
    }

    @GetMapping("/{userUUID}")
    public UserOutDTO findByUuid(@PathVariable String userUUID) {
        Optional<UserDTO> optionalUserDTO = userIntegration.findbyUserUUID(userUUID);
        optionalUserDTO.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        UserOutDTO user = new UserOutDTO(
                optionalUserDTO.get().getNickname(),
                optionalUserDTO.get().getBirthDate(),
                new HashSet<>()
        );

        Iterable<PostDTO> posts = postIntegration.findbyUserUUID(userUUID);
        for (PostDTO postDTO : posts) {
            PostOutDTO post = new PostOutDTO(
                    postDTO.getTimestamp(),
                    postDTO.getContent(),
                    new HashSet<>()
            );

            Iterable<CommentDTO> comments = commentIntegration.findbyPostUUID(postDTO.getPostUUID());
            for (CommentDTO comment : comments) {
                post.comments().add(new CommentOutDTO(
                                comment.getTimestamp(),
                                comment.getContent())
                );
            }
            user.posts().add(post);
        }
        return user;
    }
}