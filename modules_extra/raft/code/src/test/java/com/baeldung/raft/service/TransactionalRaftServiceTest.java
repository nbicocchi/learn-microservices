package com.baeldung.raft.service;

import com.baeldung.raft.persistence.model.NodeState;
import com.baeldung.raft.persistence.model.NodeStateEntity;
import com.baeldung.raft.persistence.repository.NodeStateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionalRaftServiceTest {

    @Mock
    private NodeStateRepository nodeStateRepository;

    @InjectMocks
    private TransactionalRaftService transactionalRaftService;

    @Test
    void testSaveNodeState_Success() {
        NodeStateEntity node = new NodeStateEntity();
        node.setNodeId("node1");
        node.setState(NodeState.FOLLOWER);
        node.setCurrentTerm(1);

        when(nodeStateRepository.save(any(NodeStateEntity.class))).thenReturn(Mono.just(node));

        Mono<NodeStateEntity> result = transactionalRaftService.saveNodeState(node);

        StepVerifier.create(result)
                .expectNext(node)
                .verifyComplete();

        verify(nodeStateRepository, times(1)).save(node);
    }

    @Test
    void testStepDown_Success() {
        NodeStateEntity node = new NodeStateEntity();
        node.setNodeId("node1");
        node.setState(NodeState.LEADER);
        node.setCurrentTerm(2);

        NodeStateEntity updatedNode = new NodeStateEntity();
        updatedNode.setNodeId("node1");
        updatedNode.setState(NodeState.FOLLOWER);
        updatedNode.setCurrentTerm(2);

        when(nodeStateRepository.save(any(NodeStateEntity.class))).thenReturn(Mono.just(updatedNode));

        Mono<Void> result = transactionalRaftService.stepDown(node);

        StepVerifier.create(result)
                .verifyComplete();

        ArgumentCaptor<NodeStateEntity> captor = ArgumentCaptor.forClass(NodeStateEntity.class);
        verify(nodeStateRepository, times(1)).save(captor.capture());
        NodeStateEntity savedNode = captor.getValue();
        assert savedNode.getState() == NodeState.FOLLOWER;
    }

    @Test
    void testBecomeLeader_Success() {
        NodeStateEntity node = new NodeStateEntity();
        node.setNodeId("node1");
        node.setState(NodeState.CANDIDATE);
        node.setCurrentTerm(3);

        NodeStateEntity updatedNode = new NodeStateEntity();
        updatedNode.setNodeId("node1");
        updatedNode.setState(NodeState.LEADER);
        updatedNode.setCurrentTerm(3);

        when(nodeStateRepository.save(any(NodeStateEntity.class))).thenReturn(Mono.just(updatedNode));

        Mono<NodeStateEntity> result = transactionalRaftService.becomeLeader(node);

        StepVerifier.create(result)
            .expectNext(updatedNode) // Now correctly matches based on overridden equals
                .verifyComplete();

        ArgumentCaptor<NodeStateEntity> captor = ArgumentCaptor.forClass(NodeStateEntity.class);
        verify(nodeStateRepository, times(1)).save(captor.capture());
        NodeStateEntity savedNode = captor.getValue();
        assert savedNode.getState() == NodeState.LEADER;
    }
}
