package com.valentini.userservice.resolver;
import com.valentini.userservice.exception.UsernameAlreadyExistsException;
import com.valentini.userservice.model.User;
import com.valentini.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class UserResolver {
    private final UserRepository userRepository;

    public UserResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @QueryMapping
    public User getUserById(@Argument Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public Optional<User> getUserByUsername(@Argument String username) {
        return userRepository.findByUsername(username);
    }

    @QueryMapping
    public User getUserByEmail(@Argument String email) {
        return userRepository.findByEmail(email);
    }

    @QueryMapping
    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    @MutationMapping
    public User createUser(@Argument String username, @Argument String email, @Argument String password, @Argument String avatarPath) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setAvatarPath(avatarPath);
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            // Throw a custom exception
            throw new UsernameAlreadyExistsException("Username is already taken");
        }

    }

    @MutationMapping
    public boolean deleteUser(@Argument Long id) {
        userRepository.deleteById(id);
        return true;
    }

    @MutationMapping
    public User updateUser(@Argument Long id, @Argument String username, @Argument String email, @Argument String password, @Argument String avatarPath) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setAvatarPath(avatarPath);

            try {
                return userRepository.save(user);
            } catch (DataIntegrityViolationException e) {
                // Throw a custom exception
                throw new UsernameAlreadyExistsException("Username is already taken");
            }
        }
        return null;
    }
}
