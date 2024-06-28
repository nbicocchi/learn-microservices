package com.example.zookeeper_config_service;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class ZookeeperConfigServiceApplication {

	@Value("${zookeeper.connect-string}")
	private String connectString;

	private static final int SESSION_TIMEOUT = 3000;

	public static void main(String[] args) {
		SpringApplication.run(ZookeeperConfigServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner populateZookeeper() {
		return args -> {
			ZooKeeper zooKeeper = connectToZookeeper();
			createOrUpdateNode(zooKeeper, "/config", "");

			createOrUpdateNode(zooKeeper, "/config/time-service", "");
			createOrUpdateNode(zooKeeper, "/config/time-service/configuration", "I am time-service");

			createOrUpdateNode(zooKeeper, "/config/date-service", "");
			createOrUpdateNode(zooKeeper, "/config/date-service/configuration", "I am date-service");

			createOrUpdateNode(zooKeeper, "/config/datetime-composite-service", "");
			createOrUpdateNode(zooKeeper, "/config/datetime-composite-service/configuration", "I am datetime-composite-service");

			zooKeeper.close();
		};
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

	private void createOrUpdateNode(ZooKeeper zooKeeper, String path, String data) throws KeeperException, InterruptedException {
		Stat stat = zooKeeper.exists(path, false);
		if (stat == null) {
			zooKeeper.create(path, data.getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		} else {
			zooKeeper.setData(path, data.getBytes(StandardCharsets.UTF_8), stat.getVersion());
		}
	}
}