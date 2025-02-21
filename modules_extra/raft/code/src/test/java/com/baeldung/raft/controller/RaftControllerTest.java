package com.baeldung.raft.controller;

import com.baeldung.raft.web.controller.RaftController;
import com.baeldung.raft.web.dto.NodeStatusDTO;
import com.baeldung.raft.persistence.model.NodeState;
import com.baeldung.raft.persistence.model.NodeStateEntity;
import com.baeldung.raft.service.RaftService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

@WebFluxTest(controllers = RaftController.class)
class RaftControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private RaftService raftService;

    @Test
    void testRequestVote_Success() {
        when(raftService.requestVote(anyString(), anyInt())).thenReturn(Mono.just(true));

        webTestClient.post()
                .uri("/raft/request-vote")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"candidateId\":\"node2\", \"candidateTerm\":2}")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .isEqualTo(true);

        verify(raftService, times(1)).requestVote("node2", 2);
    }

    @Test
    void testStartElection_Success() {
        when(raftService.startElection()).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/raft/start-election")
                .exchange()
                .expectStatus().isOk();

        verify(raftService, times(1)).startElection();
    }

    @Test
    void testInitializeNode_Success() {
        when(raftService.initializeNode()).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/raft/initialize")
                .exchange()
                .expectStatus().isOk();

        verify(raftService, times(1)).initializeNode();
    }

    @Test
    void testGetStatus_Success() {
        // Create and set up a NodeStateEntity with expected values
        NodeStateEntity nodeEntity = new NodeStateEntity();
        nodeEntity.setNodeId("node1");
        nodeEntity.setState(NodeState.FOLLOWER);
        nodeEntity.setCurrentTerm(1);
        nodeEntity.setVotedFor("None");

        // Mock the raftService to return the populated NodeStateEntity
        when(raftService.getNodeStatusEntity()).thenReturn(Mono.just(nodeEntity));

        // Define the expected NodeStatusDTO
        NodeStatusDTO expectedStatusDTO = new NodeStatusDTO("node1", NodeState.FOLLOWER, 1, "None", null, false);

        webTestClient.get()
                .uri("/raft/status")
                .exchange()
                .expectStatus().isOk()
                .expectBody(NodeStatusDTO.class)
                .isEqualTo(expectedStatusDTO);

        verify(raftService, times(1)).getNodeStatusEntity();
    }


    @Test
    void testStreamStatus_Success() {
        // Define the status list to be returned by the mocked service
        List<NodeStatusDTO> statusList = List.of(
                new NodeStatusDTO("node1", NodeState.LEADER, 2, "None", "localhost:8000", false),
                new NodeStatusDTO("node2", NodeState.FOLLOWER, 2, "node1", "localhost:8001", false),
                new NodeStatusDTO("node3", NodeState.FOLLOWER, 2, "node1", "localhost:8002", false)
        );

        // Mock the raftService to return the status list
        when(raftService.getAllNodeStatuses()).thenReturn(Mono.just(statusList));

        // Use WebTestClient to subscribe to the stream and expect the first emission
        webTestClient.get()
                .uri("/raft/status-stream")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .returnResult(String.class)
                .getResponseBody()
                .take(1) // Take only the first emission to prevent timeout
                .as(StepVerifier::create)
                .expectNextMatches(responseBody -> {
                    // Parse the JSON response and verify its contents
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        List<NodeStatusDTO> actualStatusList = mapper.readValue(responseBody, new TypeReference<>() {
                        });
                        return actualStatusList.equals(statusList);
                    } catch (JsonProcessingException e) {
                        return false;
                    }
                })
                .verifyComplete();

        verify(raftService, times(1)).getAllNodeStatuses();
    }
}
