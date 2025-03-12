package com.nbicocchi.user.integration;


import com.nbicocchi.user.controller.dto.PostDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PostIntegration {
    String postServiceHost;
    int postServicePort;

    public PostIntegration(
            @Value("${app.post-service.host}") String postServiceHost,
            @Value("${app.post-service.port}") int postServicePort) {
        this.postServiceHost = postServiceHost;
        this.postServicePort = postServicePort;
    }

    public Iterable<PostDTO> findbyUserUUID(String userUUID) {
        String url = "http://" + postServiceHost + ":" + postServicePort + "/posts" + "/" + userUUID;
        RestClient restClient = RestClient.builder().build();
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
