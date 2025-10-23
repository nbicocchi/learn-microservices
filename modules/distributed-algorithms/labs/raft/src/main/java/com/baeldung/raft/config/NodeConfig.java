package com.baeldung.raft.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Configuration properties for Raft node settings.
 */
@Data
@Component
@ConfigurationProperties(prefix = "node")
public class NodeConfig {
    /**
     * Unique identifier for the current node.
     */
    private String id;

    /**
     * List of cluster node URLs participating in the Raft consensus.
     */
    private List<String> clusterNodes;
}
