package com.nbicocchi.bff.integration;


import com.nbicocchi.bff.dto.CommentDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CommentIntegration {
    private final RestClient.Builder restClientBuilder;

    public CommentIntegration(RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    public Iterable<CommentDTO> findbyPostUUID(String postUUID) {
        String url = "http://COMMENT-SERVICE/comments" + "/" + postUUID;
        RestClient restClient = restClientBuilder.build();
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
