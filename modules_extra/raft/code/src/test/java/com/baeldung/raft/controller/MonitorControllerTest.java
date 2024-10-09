package com.baeldung.raft.controller;

import com.baeldung.raft.web.controller.MonitorController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(controllers = MonitorController.class)
class MonitorControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testMonitorPage_Success() {
        webTestClient.get()
                .uri("/monitor")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith("text/html")
                .expectBody(String.class)
                .consumeWith(response -> {
                    String body = response.getResponseBody();
                    assert body != null;
                    assert body.contains("Raft Node Status");
                });
    }
}
