package com.nbicocchi.user.controller;

import com.nbicocchi.user.dto.UserDTO;
import com.nbicocchi.user.persistence.model.UserModel;
import com.nbicocchi.user.persistence.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/users")
public class UserController {
    UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        return mapToDTO(optionalUserModel.get());
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