package com.baeldung.raft.repository;

import com.baeldung.raft.config.DatabaseConfig;
import com.baeldung.raft.persistence.model.NodeState;
import com.baeldung.raft.persistence.model.NodeStateEntity;
import com.baeldung.raft.persistence.repository.NodeStateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@DataR2dbcTest
@Import(DatabaseConfig.class)
class NodeStateRepositoryTest {

    @Autowired
    private NodeStateRepository nodeStateRepository;

    /**
     * Dynamically set properties required for DatabaseConfig.
     * This ensures that node.id and node.clusterNodes are set during tests.
     */
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("node.id", () -> "testnode");
        registry.add("node.clusterNodes", () -> List.of("localhost:8000", "localhost:8001"));
        registry.add("spring.r2dbc.initialization-mode", () -> "always"); // Ensure schema initialization
    }

    @Test
    void testFindByNodeId_Found() {
        NodeStateEntity node = new NodeStateEntity();
        node.setNodeId("node12");
        node.setState(NodeState.FOLLOWER);
        node.setCurrentTerm(1);
        node.setVotedFor(null);

        Mono<NodeStateEntity> saveMono = nodeStateRepository.save(node);

        StepVerifier.create(saveMono)
                .expectNextMatches(savedNode ->
                        savedNode.getNodeId().equals("node12") &&
                                savedNode.getState() == NodeState.FOLLOWER &&
                                savedNode.getVotedFor() == null)
                .verifyComplete();

        Mono<NodeStateEntity> findMono = nodeStateRepository.findByNodeId("node12");

        StepVerifier.create(findMono)
                .expectNextMatches(foundNode ->
                        foundNode.getNodeId().equals("node12") &&
                                foundNode.getState() == NodeState.FOLLOWER &&
                                foundNode.getVotedFor() == null)
                .verifyComplete();
    }

    @Test
    void testFindByNodeId_NotFound() {
        Mono<NodeStateEntity> findMono = nodeStateRepository.findByNodeId("nonexistent");

        StepVerifier.create(findMono)
                .verifyComplete();
    }
}
