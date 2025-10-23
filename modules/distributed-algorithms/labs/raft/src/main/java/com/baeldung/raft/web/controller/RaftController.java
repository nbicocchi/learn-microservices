package com.baeldung.raft.web.controller;

import com.baeldung.raft.persistence.model.NodeState;
import com.baeldung.raft.web.dto.NodeStatusDTO;
import com.baeldung.raft.service.RaftService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.Duration;
import java.util.Map;

/**
 * REST controller exposing endpoints for Raft consensus operations.
 */
@RestController
@Slf4j
@RequestMapping("/raft")
@Tag(name = "Raft Operations", description = "Endpoints for Raft consensus operations")
public class RaftController {
    private final RaftService raftService;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a new {@code RaftController} with the specified Raft service.
     *
     * @param raftService the service handling Raft operations
     */
    public RaftController(RaftService raftService) {
        this.raftService = raftService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Starts a new election in the Raft cluster.
     *
     * @return a {@link Mono} signaling completion
     */
    @Operation(summary = "Start a new election")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Election started successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/start-election")
    public Mono<Void> startElection() {
        return raftService.startElection();
    }

    /**
     * Handles a vote request from a candidate node.
     *
     * @param payload the vote request payload containing candidate details
     * @return a {@link Mono} emitting {@code true} if the vote is granted, {@code false} otherwise
     */
    @Operation(summary = "Request a vote from the node")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vote granted or denied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @PostMapping("/request-vote")
    public Mono<Boolean> requestVote(
            @Parameter(description = "Vote request payload", required = true)
            @RequestBody Map<String, Object> payload) {
        // Recover the candidateId and candidateTerm from the payload
        String candidateId = (String) payload.get("candidateId");
        Integer candidateTerm = (payload.get("candidateTerm") instanceof Integer) ? (Integer) payload.get("candidateTerm") : null;

        // Verify that the candidateId and candidateTerm are not null
        if (candidateId == null || candidateTerm == null) {
            return Mono.error(new IllegalArgumentException("Invalid request payload: 'candidateId' or 'candidateTerm' is missing."));
        }
        return raftService.requestVote(candidateId, candidateTerm);
    }

    /**
     * Initializes the node within the Raft cluster.
     *
     * @return a {@link Mono} signaling completion
     */
    @Operation(summary = "Initialize the node")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Node initialized successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/initialize")
    public Mono<Void> initialize() {
        return raftService.initializeNode();
    }

    /**
     * Receives a heartbeat signal from the leader node.
     *
     * @return a {@link Mono} signaling completion
     */
    @Operation(summary = "Receive heartbeat from the leader")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Heartbeat received successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/heartbeat")
    public Mono<Void> receiveHeartbeat() {
        return raftService.receiveHeartbeat();
    }


    /**
     * Stops the node, transitioning it to the DOWN state.
     *
     * @return a {@link Mono} emitting a message indicating the node has been stopped
     */
    @Operation(summary = "Stop the node")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Node stopped successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/stop")
    public Mono<String> stopNode() {
        return raftService.stopNode()
                .thenReturn("Node has been stopped and is now in DOWN state.");
    }

    /**
     * Resumes the node, transitioning it to the ACTIVE state.
     *
     * @return a {@link Mono} emitting a message indicating the node has been resumed
     */
    @Operation(summary = "Resume the node")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Node resumed successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/resume")
    public Mono<String> resumeNode() {
        return raftService.resumeNode()
                .thenReturn("Node has been resumed and is now active.");
    }

    /**
     * Retrieves the current status of the node.
     *
     * @return a {@link Mono} emitting {@link NodeStatusDTO} with the node's status
     */
    @Operation(summary = "Get the current node status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Node status retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NodeStatusDTO.class))),
            @ApiResponse(responseCode = "404", description = "Node state not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @GetMapping("/status")
    public Mono<NodeStatusDTO> getStatus() {
        return raftService.getNodeStatusEntity()
                .map(node -> {
                    NodeState state = node.isStopped() ? NodeState.DOWN : node.getState();
                    return new NodeStatusDTO(
                            node.getNodeId(),
                            state,
                            node.getCurrentTerm(),
                            node.getVotedFor(),
                            raftService.getOwnNodeUrl(),
                            node.isStopped()
                    );
                });
    }

    /**
     * Streams the status of all nodes in the Raft cluster using Server-Sent Events.
     *
     * @return a {@link Flux} emitting JSON strings representing node statuses
     */
    @Operation(summary = "Stream the status of all nodes in the cluster")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Streaming node statuses",
                    content = @Content(mediaType = "text/event-stream")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @GetMapping(value = "/status-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamStatus() {
        return Flux.interval(Duration.ofMillis(500)).flatMap(tick -> raftService.getAllNodeStatuses()).map(nodeStates -> {
            // Convert the list of NodeStatusDTO objects to a JSON string
            try {
                return objectMapper.writeValueAsString(nodeStates);
            } catch (JsonProcessingException e) {
                log.error("Error serializing node statuses: {}", e.getMessage());
                return "[]";
            }
        });
    }
}