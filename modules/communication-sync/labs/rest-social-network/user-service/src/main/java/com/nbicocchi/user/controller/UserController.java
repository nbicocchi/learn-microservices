package com.nbicocchi.user.controller;

import com.nbicocchi.user.controller.dto.PostDTO;
import com.nbicocchi.user.controller.dto.UserDTO;
import com.nbicocchi.user.integration.PostIntegration;
import com.nbicocchi.user.persistence.model.UserModel;
import com.nbicocchi.user.persistence.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/users")
public class UserController {
    UserRepository userRepository;
    PostIntegration postIntegration;

    public UserController(UserRepository userRepository, PostIntegration postIntegration) {
        this.userRepository = userRepository;
        this.postIntegration = postIntegration;
    }

    @GetMapping
    public Iterable<UserDTO> findAll() {
        Iterable<UserModel> foundUsers = userRepository.findAll();
        return mapToDTO(foundUsers);
    }

    @GetMapping("/{userUUID}")
    public UserDTO findByUuid(@PathVariable String userUUID) {
        Optional<UserModel> optionalUserModel = userRepository.findByUserUUID(userUUID);
        optionalUserModel.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        UserDTO userDTO = mapToDTO(optionalUserModel.get());

        Iterable<PostDTO> posts = postIntegration.findbyUserUUID(userUUID);
        for (PostDTO postDTO : posts) {
            userDTO.getPosts().add(postDTO);
        }

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