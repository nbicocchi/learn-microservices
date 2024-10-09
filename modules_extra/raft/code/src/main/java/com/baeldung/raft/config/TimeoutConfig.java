package com.baeldung.raft.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@Builder
@AllArgsConstructor
@ConfigurationProperties(prefix = "raft")
public class TimeoutConfig {
    private ElectionTimeout electionTimeout;
    private long heartbeatInterval;

    @Setter
    @Getter
    @Builder
    @AllArgsConstructor
    public static class ElectionTimeout {
        private long min;
        private long max;

    }

}
