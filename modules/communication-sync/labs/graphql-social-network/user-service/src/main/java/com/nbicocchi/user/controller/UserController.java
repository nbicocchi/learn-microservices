package com.nbicocchi.user.controller;

import com.nbicocchi.user.dto.PostDTO;
import com.nbicocchi.user.dto.UserDTO;
import com.nbicocchi.user.dto.UserDTOInput;
import com.nbicocchi.user.integration.PostIntegration;
import com.nbicocchi.user.persistence.model.UserModel;
import com.nbicocchi.user.persistence.repository.UserRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final PostIntegration postIntegration;

    public UserController(UserRepository userRepository, PostIntegration postIntegration) {
        this.userRepository = userRepository;
        this.postIntegration = postIntegration;
    }

    // -------------------------------
    // Queries
    // -------------------------------
    @QueryMapping
    public List<UserDTO> allUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public UserDTO userByUUID(@Argument String userUUID) {
        UserModel userModel = userRepository.findByUserUUID(userUUID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserDTO userDTO = mapToDTO(userModel);

        Iterable<PostDTO> posts = postIntegration.findbyUserUUID(userUUID);
        Set<PostDTO> postSet = StreamSupport.stream(posts.spliterator(), false)
                .collect(Collectors.toSet());

        userDTO.setPosts(postSet);
        return userDTO;
    }

    // -------------------------------
    // Mutations
    // -------------------------------
    @MutationMapping
    public UserDTO addUser(@Argument UserDTOInput input) {
        // Check if userUUID already exists
        if (userRepository.findByUserUUID(input.getUserUUID()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }

        UserModel savedUser = userRepository.save(mapToModel(input));
        return mapToDTO(savedUser);
    }


    @MutationMapping
    public Boolean removeUser(@Argument String userUUID) {
        Optional<UserModel> optionalUser = userRepository.findByUserUUID(userUUID);
        if (optionalUser.isEmpty()) {
            return false;
        }

        // Optionally remove posts
        // postIntegration.deletePostsByUserUUID(userUUID);

        userRepository.delete(optionalUser.get());
        return true;
    }

    // -------------------------------
    // Mapping helpers
    // -------------------------------
    private UserDTO mapToDTO(UserModel user) {
        UserDTO dto = new UserDTO(
                user.getUserUUID(),
                user.getNickname(),
                user.getBirthDate()
        );
        dto.setPosts(new HashSet<>()); // Initialize posts as empty
        return dto;
    }

    private UserModel mapToModel(UserDTOInput dto) {
        UserModel user = new UserModel();
        user.setUserUUID(dto.getUserUUID());
        user.setNickname(dto.getNickname());

        if (dto.getBirthDate() != null && !dto.getBirthDate().isEmpty()) {
            user.setBirthDate(LocalDate.parse(dto.getBirthDate()));
        } else {
            user.setBirthDate(null);
        }
        return user;
    }

}
