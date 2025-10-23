package com.baeldung.raft.persistence.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enumeration of possible node states in the Raft cluster.
 */
@Schema(description = "Enumeration of possible node states in the Raft cluster.")
public enum NodeState {
    @Schema(description = "Follower state")
    FOLLOWER,

    @Schema(description = "Leader state")
    LEADER,

    @Schema(description = "Candidate state")
    CANDIDATE,

    @Schema(description = "Down state")
    DOWN
}
