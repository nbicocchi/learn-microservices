package com.baeldung.raft.service;

import com.baeldung.raft.config.NodeConfig;
import com.baeldung.raft.config.TimeoutConfig;
import com.baeldung.raft.persistence.model.NodeState;
import com.baeldung.raft.persistence.model.NodeStateEntity;
import com.baeldung.raft.persistence.repository.NodeStateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RaftServiceTest {

    @Mock
    private NodeStateRepository nodeStateRepository;

    @Mock
    private TransactionalRaftService transactionalRaftService;

    @Mock
    private NodeConfig nodeConfig;

    private RaftService raftService;

    @BeforeEach
    void setUp() {
        // Define the behavior of the mocked NodeConfig
        String nodeId = "node1";
        List<String> clusterNodes = List.of("localhost:8000", "localhost:8001", "localhost:8002");
        int serverPort = 8000;

        // Update NodeConfig mock
        when(nodeConfig.getId()).thenReturn(nodeId);
        when(nodeConfig.getClusterNodes()).thenReturn(clusterNodes);

        // Instantiate RaftTimeoutProperties using builder
        TimeoutConfig.ElectionTimeout electionTimeout = TimeoutConfig.ElectionTimeout.builder()
                .min(150)
                .max(300)
                .build();

        TimeoutConfig timeoutConfig = TimeoutConfig.builder()
                .electionTimeout(electionTimeout)
                .heartbeatInterval(50)
                .build();

        // Instantiate RaftService with mocked dependencies and predefined values
        RaftService realRaftService = new RaftService(
                nodeStateRepository,
                transactionalRaftService,
                nodeConfig,
                timeoutConfig,
                serverPort
        );

        // Create a spy of RaftService to allow partial mocking
        raftService = spy(realRaftService);

        ReflectionTestUtils.setField(raftService, "ownNodeUrl", "localhost:8000");
    }

    @Test
    void testInitializeNode_NodeDoesNotExist_ShouldCreateFollower() {
        NodeStateEntity newNode = new NodeStateEntity();
        newNode.setNodeId("node1");
        newNode.setState(NodeState.FOLLOWER);
        newNode.setCurrentTerm(0);

        // Mock the repository to return empty when searching for the node
        when(nodeStateRepository.findByNodeId("node1")).thenReturn(Mono.empty());

        // Mock the transactional service to save and return the new node
        when(transactionalRaftService.saveNodeState(any(NodeStateEntity.class))).thenReturn(Mono.just(newNode));

        // Mock the checkClusterReadiness to return true
        when(raftService.checkClusterReadiness()).thenReturn(Mono.just(true));

        // Act
        Mono<Void> result = raftService.initializeNode();

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        // Verify interactions
        verify(nodeStateRepository, times(1)).findByNodeId("node1");
        verify(transactionalRaftService, times(1)).saveNodeState(any(NodeStateEntity.class));
        verify(raftService, times(1)).checkClusterReadiness();
    }

    @Test
    void testRequestVote_CandidateTermHigher_ShouldGrantVote() {
        NodeStateEntity currentNode = new NodeStateEntity();
        currentNode.setNodeId("node1");
        currentNode.setState(NodeState.FOLLOWER);
        currentNode.setCurrentTerm(1);
        currentNode.setVotedFor(null);

        NodeStateEntity updatedNode = new NodeStateEntity();
        updatedNode.setNodeId("node1");
        updatedNode.setState(NodeState.FOLLOWER);
        updatedNode.setCurrentTerm(2);
        updatedNode.setVotedFor("node2");

        // Mock the repository to return the current node state
        when(nodeStateRepository.findByNodeId("node1")).thenReturn(Mono.just(currentNode));

        // Mock the transactional service to save and return the updated node
        when(transactionalRaftService.saveNodeState(any(NodeStateEntity.class))).thenReturn(Mono.just(updatedNode));

        // Act
        Mono<Boolean> voteResult = raftService.requestVote("node2", 2);

        // Assert
        StepVerifier.create(voteResult)
                .expectNext(true)
                .verifyComplete();

        // Verify interactions
        verify(nodeStateRepository, times(1)).findByNodeId("node1");
        verify(transactionalRaftService, times(1)).saveNodeState(any(NodeStateEntity.class));
    }

    @Test
    void testRequestVote_CandidateTermLower_ShouldRejectVote() {
        NodeStateEntity currentNode = new NodeStateEntity();
        currentNode.setNodeId("node1");
        currentNode.setState(NodeState.FOLLOWER);
        currentNode.setCurrentTerm(3);
        currentNode.setVotedFor(null);

        // Mock the repository to return the current node state
        when(nodeStateRepository.findByNodeId("node1")).thenReturn(Mono.just(currentNode));

        // Act
        Mono<Boolean> voteResult = raftService.requestVote("node2", 2);

        // Assert
        StepVerifier.create(voteResult)
                .expectNext(false)
                .verifyComplete();

        // Verify interactions
        verify(nodeStateRepository, times(1)).findByNodeId("node1");
        verify(transactionalRaftService, never()).saveNodeState(any(NodeStateEntity.class));
    }
}
