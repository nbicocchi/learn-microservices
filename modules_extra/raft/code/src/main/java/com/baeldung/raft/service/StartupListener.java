package com.baeldung.raft.service;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * Listener that performs actions when the application is fully started.
 */
@Component
@Slf4j
public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {

    private final RaftService raftService;

    /**
     * Constructs a new {@code StartupListener} with the specified Raft service.
     *
     * @param raftService the service handling Raft operations
     */
    public StartupListener(RaftService raftService) {
        this.raftService = raftService;
    }

    /**
     * Handles the application ready event by logging startup information and initializing the node.
     *
     * @param event the {@link ApplicationReadyEvent} indicating that the application is ready
     */
    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        log.info("\n\n");
        log.info("****************************************************************************************");
        log.info("Node is starting up with the following configuration:");
        log.info("\tNode ID: {}", raftService.getNodeId());
        log.info("\tNode URL: {}", raftService.getOwnNodeUrl());
        log.info("\tCluster Nodes: {}", String.join(", ", raftService.getClusterNodes()));
        log.info("\tElection Timeout: {}-{} ms", raftService.getTimeoutProperties().getElectionTimeout().getMin(), raftService.getTimeoutProperties().getElectionTimeout().getMax());
        log.info("\tHeartbeat Timeout: {} ms", raftService.getTimeoutProperties().getHeartbeatInterval());
        log.debug("\tDEBUGGING MONITOR: http://{}/monitor", raftService.getOwnNodeUrl());
        log.info("****************************************************************************************\n\n");

        // Initialize the node
        raftService.initializeNode()
                .doOnError(e -> log.error("Error during node initialization: {}", e.getMessage()))
                .subscribe();
    }
}
