package com.nbicocchi.user.controller;

import com.nbicocchi.user.dto.PostDTO;
import com.nbicocchi.user.dto.UserDTO;
import com.nbicocchi.user.integration.PostIntegration;
import com.nbicocchi.user.persistence.model.UserModel;
import com.nbicocchi.user.persistence.repository.UserRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * query {
 *   allUsers {
 *     userUUID
 *     nickname
 *     birthDate
 *   }
 * }
 *
 * query {
 *   userByUUID(userUUID: "171f5df0-b213-4a40-8ae6-fe82239ab660") {
 *     userUUID
 *     nickname
 *     birthDate
 *     posts {
 *       userUUID
 *       timestamp
 *       content
 *     }
 *   }
 * }
 *
 *
 */

@RestController
@RequestMapping("/users")
public class UserController {
    UserRepository userRepository;
    PostIntegration postIntegration;

    public UserController(UserRepository userRepository, PostIntegration postIntegration) {
        this.userRepository = userRepository;
        this.postIntegration = postIntegration;
    }

    @QueryMapping
    public List<UserDTO> allUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public UserDTO userByUUID(@Argument String userUUID) {
        Optional<UserModel> optionalUserModel = userRepository.findByUserUUID(userUUID);
        optionalUserModel.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        UserDTO userDTO = mapToDTO(optionalUserModel.get());

        Iterable<PostDTO> posts = postIntegration.findbyUserUUID(userUUID);

        // Convert Iterable to Set
        Set<PostDTO> postSet = StreamSupport.stream(posts.spliterator(), false)
                .collect(Collectors.toSet());

        userDTO.setPosts(postSet);

        return userDTO;
    }

    private UserDTO mapToDTO(UserModel user) {
        return new UserDTO(
                user.getUserUUID(),
                user.getNickname(),
                user.getBirthDate()
        );
    }

    private Iterable<UserDTO> mapToDTO(Iterable<UserModel> users) {
        return StreamSupport.stream(users.spliterator(), false)
                .map(u -> new UserDTO(u.getUserUUID(), u.getNickname(), u.getBirthDate()))
                .collect(Collectors.toList());
    }
}