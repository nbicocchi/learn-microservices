package com.baeldung.raft.web.dto;

import com.baeldung.raft.persistence.model.NodeState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing the status of a Raft node.
 */
@Data
@NoArgsConstructor
public class NodeStatusDTO {
    @Schema(description = "Unique identifier of the node", example = "node1")
    private String nodeId;

    @Schema(description = "Current state of the node in the Raft cluster", example = "FOLLOWER")
    private NodeState state;

    @Schema(description = "Current term number of the node", example = "1")
    private int currentTerm;

    @Schema(description = "ID of the node this node has voted for in the current term", example = "node2")
    private String votedFor;

    @Schema(description = "URL of the node", example = "localhost:8000")
    private String nodeUrl;

    @Schema(description = "Flag indicating if the node is stopped", example = "false")
    private boolean isStopped;

    /**
     * Constructs a new {@code NodeStatusDTO} with the specified details.
     *
     * @param nodeId      the unique identifier of the node
     * @param state       the current state of the node
     * @param currentTerm the current term number
     * @param votedFor    the ID of the node voted for in the current term
     * @param nodeUrl     the URL of the node
     * @param isStopped   the stopped status of the node
     */
    public NodeStatusDTO(String nodeId, NodeState state, int currentTerm, String votedFor, String nodeUrl, boolean isStopped) {
        this.nodeId = nodeId;
        this.state = state;
        this.currentTerm = currentTerm;
        this.votedFor = votedFor;
        this.nodeUrl = nodeUrl;
        this.isStopped = isStopped;
    }
}
