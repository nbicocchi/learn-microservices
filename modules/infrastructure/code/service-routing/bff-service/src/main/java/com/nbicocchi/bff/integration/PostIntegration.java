package com.nbicocchi.bff.integration;


import com.nbicocchi.bff.dto.PostDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PostIntegration {
    private final RestClient.Builder restClientBuilder;

    public PostIntegration(RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    public Iterable<PostDTO> findbyUserUUID(String userUUID) {
        String url = "http://POST-SERVICE/posts" + "/" + userUUID;
        RestClient restClient = restClientBuilder.build();
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
