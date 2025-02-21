package com.baeldung.raft.persistence.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

/**
 * Entity representing the state of a Raft node in the database.
 */
@Setter
@Getter
@Table("node_state")
@EqualsAndHashCode
public class NodeStateEntity {
    @Id
    @Schema(description = "Unique identifier of the record", example = "1")
    private Long id;

    @Column("node_id")
    @Schema(description = "Unique identifier of the node", example = "node1")
    private String nodeId;

    @Enumerated(EnumType.STRING)
    @Column("state")
    @Schema(description = "Current state of the node", example = "FOLLOWER")
    private NodeState state;

    @Column("current_term")
    @Schema(description = "Current term number", example = "1")
    private int currentTerm;

    @Column("voted_for")
    @Schema(description = "Node ID that this node has voted for", example = "node2")
    private String votedFor;

    @Column("is_stopped")
    @Schema(description = "Flag indicating if the node is stopped", example = "false")
    private boolean isStopped;

    @Override
    public String toString() {
        return "NodeStateEntity{" +
                "id=" + id +
                ", nodeId='" + nodeId + '\'' +
                ", state=" + state +
                ", currentTerm=" + currentTerm +
                ", votedFor='" + votedFor + '\'' +
                ", isStopped=" + isStopped +
                '}';
    }

    // Lombok seems not working for this setter...
    public void setIsStopped(boolean isStopped) {
        this.isStopped = isStopped;
    }
}
