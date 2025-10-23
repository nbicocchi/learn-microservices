package com.baeldung.raft.persistence.repository;

import lombok.NonNull;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import com.baeldung.raft.persistence.model.NodeStateEntity;

/**
 * Repository interface for performing CRUD operations on NodeStateEntity.
 */
public interface NodeStateRepository extends ReactiveCrudRepository<NodeStateEntity, Long> {

    /**
     * Finds a node state entity by its node ID.
     *
     * @param nodeId the unique identifier of the node
     * @return a {@link Mono} emitting the found {@link NodeStateEntity}, or empty if not found
     */
    Mono<NodeStateEntity> findByNodeId(String nodeId);

    /**
     * Retrieves all node state entities in the repository.
     *
     * @return a {@link Flux} emitting all {@link NodeStateEntity} instances
     */
    @Override
    @NonNull
    Flux<NodeStateEntity> findAll();
}


