# Raft Consensus Algorithm - Application

## Table of Contents

- [Introduction](#introduction)
- [Application Structure](#application-structure)
- [Key Components in Code](#key-components-in-code)
- [Leader Election in Code](#leader-election-in-code)
- [Handling Heartbeats](#handling-heartbeats)
- [Log Replication in Code](#log-replication-in-code)
- [Conclusion](#conclusion)
- [References](#references)

---

## Introduction

Implementing the Raft consensus algorithm within a Spring Boot application involves orchestrating various components to
achieve reliable consensus, maintain consistent state across nodes, and ensure fault tolerance. This section provides an
overview of how to build a Raft-based system using Spring Boot, highlighting essential code snippets that illustrate the
fundamental aspects of the implementation.

## Application Structure

The Raft implementation is organized into several key packages, each responsible for different aspects of the algorithm:

- **Configuration**: Manages node settings and API documentation.
- **Web Controller**: Exposes RESTful endpoints for Raft operations.
- **Service**: Contains the core logic for Raft functionalities like leader election and log replication.
- **Persistence**: Handles the storage and retrieval of node states.
- **Exception Handling**: Provides global exception management to ensure robustness.

## Key Components in Code

### Node Configuration

Node configuration is crucial for initializing each node with its unique identifier and cluster details. Spring Boot's
`@ConfigurationProperties` is used to bind external configurations to Java objects, facilitating easy management of node
settings.

```java

@Data
@Component
@ConfigurationProperties(prefix = "node")
public class NodeConfig {
    private String id;
    private List<String> clusterNodes;
}
```

*This class binds the `node.id` and `node.cluster-nodes` properties from the application's configuration to
the `NodeConfig` object.*

### Raft Service

The `RaftService` class encapsulates the core Raft logic, including leader election, log replication, and heartbeat
management. It interacts with the persistence layer to manage node states and uses `WebClient` for inter-node
communication.

```java

@Service
@Slf4j
public class RaftService {
    private final NodeStateRepository nodeStateRepository;
    private final TransactionalRaftService transactionalRaftService;
    private final WebClient webClient;

    @Getter
    private final String nodeId;
    @Getter
    private final List<String> clusterNodes;

    public RaftService(NodeStateRepository nodeStateRepository,
                       TransactionalRaftService transactionalRaftService,
                       NodeConfig nodeConfig,
                       @Value("${server.port}") int serverPort) {
        this.nodeStateRepository = nodeStateRepository;
        this.transactionalRaftService = transactionalRaftService;
        this.nodeId = nodeConfig.getId();
        this.clusterNodes = nodeConfig.getClusterNodes();
        this.webClient = WebClient.create();
    }

    public Mono<Void> startElection() {
        return nodeStateRepository.findByNodeId(nodeId).flatMap(node -> {
            node.setState(NodeState.CANDIDATE);
            node.setCurrentTerm(node.getCurrentTerm() + 1);
            node.setVotedFor(nodeId);
            return transactionalRaftService.saveNodeState(node)
                    .then(sendRequestVoteToOtherNodes(node));
        });
    }

    private Mono<Void> sendRequestVoteToOtherNodes(NodeStateEntity node) {
        Map<String, Object> voteRequest = Map.of(
                "candidateId", node.getNodeId(),
                "candidateTerm", node.getCurrentTerm()
        );
        return Flux.fromIterable(clusterNodes)
                .filter(nodeUrl -> !nodeUrl.equals(ownNodeUrl))
                .flatMap(nodeUrl -> webClient.post()
                        .uri("http://" + nodeUrl + "/raft/request-vote")
                        .bodyValue(voteRequest)
                        .retrieve()
                        .bodyToMono(Boolean.class)
                        .onErrorResume(e -> Mono.just(false))
                )
                .collectList()
                .flatMap(votes -> {
                    long positiveVotes = votes.stream().filter(v -> v).count() + 1; // Include self vote
                    if (positiveVotes > clusterNodes.size() / 2) {
                        return transactionalRaftService.becomeLeader(node).then();
                    }
                    return Mono.empty();
                });
    }
}
```

*This service handles the initiation of elections, sending vote requests, and transitioning to the leader state upon
receiving majority votes.*

### Controllers

Controllers expose RESTful endpoints that allow nodes to interact with each other and with external clients. They
facilitate operations such as starting elections, handling vote requests, and receiving heartbeats.

```java

@RestController
@RequestMapping("/raft")
public class RaftController {
    private final RaftService raftService;

    public RaftController(RaftService raftService) {
        this.raftService = raftService;
    }

    @PostMapping("/start-election")
    public Mono<Void> startElection() {
        return raftService.startElection();
    }

    @PostMapping("/request-vote")
    public Mono<Boolean> requestVote(@RequestBody Map<String, Object> payload) {
        String candidateId = (String) payload.get("candidateId");
        Integer candidateTerm = (Integer) payload.get("candidateTerm");
        if (candidateId == null || candidateTerm == null) {
            return Mono.error(new IllegalArgumentException("Missing candidateId or candidateTerm"));
        }
        return raftService.requestVote(candidateId, candidateTerm);
    }
}
```

*The `RaftController` provides endpoints to start elections and handle vote requests, enabling communication between
nodes.*

### Persistence Layer

The persistence layer ensures that each node's state is stored and retrievable, which is essential for maintaining
consistency and recovering from failures. It utilizes Spring Data's reactive repositories to interact with the database.

```java

@Setter
@Getter
@Table("node_state")
public class NodeStateEntity {
    @Id
    private Long id;
    @Column("node_id")
    private String nodeId;
    @Enumerated(EnumType.STRING)
    @Column("state")
    private NodeState state;
    @Column("current_term")
    private int currentTerm;
    @Column("voted_for")
    private String votedFor;
}
```

*This entity maps to the `node_state` table, storing essential information about each node's state within the cluster.*

```java
public interface NodeStateRepository extends ReactiveCrudRepository<NodeStateEntity, Long> {
    Mono<NodeStateEntity> findByNodeId(String nodeId);
}
```

*The repository interface provides methods to query and manipulate node state entities.*

## Leader Election in Code

Leader election is a pivotal phase in Raft where nodes coordinate to elect a single leader. The process involves
transitioning to a candidate state, requesting votes, and becoming a leader upon securing majority votes.

```java
public Mono<Void> startElection() {
    // ... already shown in RaftService snippet ...
}

public Mono<Boolean> requestVote(String candidateId, int candidateTerm) {
    return nodeStateRepository.findByNodeId(nodeId).flatMap(node -> {
        if (candidateTerm > node.getCurrentTerm()) {
            node.setCurrentTerm(candidateTerm);
            node.setVotedFor(candidateId);
            node.setState(NodeState.FOLLOWER);
            return transactionalRaftService.saveNodeState(node)
                    .thenReturn(true);
        } else if (candidateTerm == node.getCurrentTerm() &&
                (node.getVotedFor() == null || node.getVotedFor().equals(candidateId))) {
            node.setVotedFor(candidateId);
            node.setState(NodeState.FOLLOWER);
            return transactionalRaftService.saveNodeState(node)
                    .thenReturn(true);
        }
        return Mono.just(false);
    });
}
```

*These methods handle the initiation of elections and the processing of incoming vote requests, ensuring that only one
leader is elected per term.*

## Handling Heartbeats

Heartbeats are periodic signals sent by the leader to maintain its authority and prevent followers from initiating new
elections. They are essential for the stability of the cluster.

```java
public Mono<Void> sendHeartbeat() {
    return Flux.fromIterable(clusterNodes)
            .filter(nodeUrl -> !nodeUrl.equals(ownNodeUrl))
            .flatMap(nodeUrl -> {
                HeartbeatRequest heartbeat = new HeartbeatRequest(nodeId, currentTerm);
                return webClient.post()
                        .uri("http://" + nodeUrl + "/raft/heartbeat")
                        .bodyValue(heartbeat)
                        .retrieve()
                        .bodyToMono(Void.class)
                        .onErrorResume(e -> Mono.empty());
            })
            .then();
}

@PostConstruct
public void scheduleHeartbeats() {
    Flux.interval(Duration.ofMillis(500))
            .filter(tick -> isLeader())
            .flatMap(tick -> sendHeartbeat())
            .subscribe();
}
```

*The leader periodically sends heartbeats to all followers to reaffirm its leadership and prevent followers from
initiating new elections.*

## Log Replication in Code

Log replication ensures that the leader's log entries are consistently mirrored across all follower nodes, maintaining a
unified state across the cluster.

```java
public Mono<Void> replicateLogEntries(String command) {
    return nodeStateRepository.findByNodeId(nodeId).flatMap(leader -> {
        leader.getLog().add(new LogEntry(leader.getCurrentTerm(), command));
        return transactionalRaftService.saveNodeState(leader)
                .then(sendAppendEntriesToFollowers());
    });
}

private Mono<Void> sendAppendEntriesToFollowers() {
    return Flux.fromIterable(clusterNodes)
            .filter(nodeUrl -> !nodeUrl.equals(ownNodeUrl))
            .flatMap(nodeUrl -> {
                AppendEntriesRequest request = new AppendEntriesRequest(
                        nodeId, currentTerm, leaderCommit, leaderLog
                );
                return webClient.post()
                        .uri("http://" + nodeUrl + "/raft/append-entries")
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(AppendEntriesResponse.class)
                        .onErrorResume(e -> Mono.empty());
            })
            .then();
}
```

*These snippets demonstrate how the leader appends new commands to its log and propagates these entries to all follower
nodes via `AppendEntries` RPCs.*

## Conclusion

Implementing the Raft consensus algorithm within a Spring Boot application involves a coordinated effort across various
components, including configuration management, service logic, controllers, and persistence layers. By leveraging Spring
Boot's robust features and reactive programming paradigms, developers can build a resilient and scalable Raft-based
system. The provided code snippets illustrate the fundamental aspects of leader election, log replication, and heartbeat
management, offering a practical guide for developing distributed consensus mechanisms using Spring Boot.

---

## References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Reactive Programming with Spring](https://spring.io/reactive)
- [Project Lombok](https://projectlombok.org/)
- [WebClient Documentation](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/function/client/WebClient.html)

