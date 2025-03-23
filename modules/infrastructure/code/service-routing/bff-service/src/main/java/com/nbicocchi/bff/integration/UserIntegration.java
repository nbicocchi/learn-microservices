package com.nbicocchi.bff.integration;

import com.nbicocchi.bff.dto.UserDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class UserIntegration {
    private final RestClient.Builder restClientBuilder;

    public UserIntegration(RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    public Optional<UserDTO> findbyUserUUID(String userUUID) {
        String url = "http://USER-SERVICE/users" + "/" + userUUID;
        RestClient restClient = restClientBuilder.build();
        UserDTO user = restClient.get()
                .uri(url)
                .retrieve()
                .body(UserDTO.class);
        return Optional.ofNullable(user);
    }
}
