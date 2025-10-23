package com.baeldung.raft.service;

import com.baeldung.raft.persistence.model.NodeState;
import com.baeldung.raft.persistence.model.NodeStateEntity;
import com.baeldung.raft.persistence.repository.NodeStateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Service handling transactional operations related to Raft node states.
 */
@Service
@Slf4j
public class TransactionalRaftService {
    private final NodeStateRepository nodeStateRepository;

    /**
     * Constructs a new {@code TransactionalRaftService} with the specified repository.
     *
     * @param nodeStateRepository the repository for node state entities
     */
    public TransactionalRaftService(NodeStateRepository nodeStateRepository) {
        this.nodeStateRepository = nodeStateRepository;
    }

    /**
     * Saves the given node state entity within a transactional context.
     *
     * @param node the {@link NodeStateEntity} to save
     * @return a {@link Mono} emitting the saved {@link NodeStateEntity}
     */
    @Transactional
    public Mono<NodeStateEntity> saveNodeState(NodeStateEntity node) {
        return nodeStateRepository.save(node)
                .doOnSuccess(savedNode -> log.info("Node state saved: {}", savedNode))
                .doOnError(e -> log.error("Error saving node state: {}", e.getMessage()));
    }

    /**
     * Steps down the node to FOLLOWER state within a transactional context.
     *
     * @param node the {@link NodeStateEntity} representing the node
     * @return a {@link Mono} signaling completion
     */
    @Transactional
    public Mono<Void> stepDown(NodeStateEntity node) {
        node.setState(NodeState.FOLLOWER);
        return nodeStateRepository.save(node)
                .doOnSuccess(savedNode -> log.info("Node {} has stepped down to FOLLOWER", savedNode.getNodeId()))
                .then();
    }

    /**
     * Promotes the node to LEADER state within a transactional context.
     *
     * @param node the {@link NodeStateEntity} representing the node
     * @return a {@link Mono} emitting the updated {@link NodeStateEntity}
     */
    @Transactional
    public Mono<NodeStateEntity> becomeLeader(NodeStateEntity node) {
        node.setState(NodeState.LEADER);
        return nodeStateRepository.save(node)
                .doOnSuccess(savedNode -> log.info("Node {} has become LEADER with term {}", savedNode.getNodeId(), savedNode.getCurrentTerm()))
                .thenReturn(node);
    }
}
