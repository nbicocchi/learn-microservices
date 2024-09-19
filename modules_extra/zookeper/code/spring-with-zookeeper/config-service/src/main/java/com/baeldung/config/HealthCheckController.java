package com.baeldung.config;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@RestController
public class HealthCheckController {

    @Value("${spring.cloud.zookeeper.connect-string}")
    private String connectString;

    private static final int SESSION_TIMEOUT = 3000;

    @GetMapping("/health")
    public String healthCheck() {
        try {
            ZooKeeper zooKeeper = connectToZookeeper();
            if (checkNodeExists(zooKeeper, "/config/time-service") &&
                    checkNodeExists(zooKeeper, "/config/date-service") &&
                    checkNodeExists(zooKeeper, "/config/datetime-composite-service")) {
                zooKeeper.close();
                return "OK";
            } else {
                zooKeeper.close();
                return "NOT_READY";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    private ZooKeeper connectToZookeeper() throws IOException, InterruptedException {
        CountDownLatch connectedSignal = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper(connectString, SESSION_TIMEOUT, event -> {
            if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                connectedSignal.countDown();
            }
        });
        connectedSignal.await();
        return zooKeeper;
    }

    private boolean checkNodeExists(ZooKeeper zooKeeper, String path) throws KeeperException, InterruptedException {
        return zooKeeper.exists(path, false) != null;
    }
}