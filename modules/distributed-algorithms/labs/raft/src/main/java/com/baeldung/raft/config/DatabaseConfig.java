package com.baeldung.raft.config;

import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.core.io.ClassPathResource;
import io.r2dbc.spi.ConnectionFactory;

/**
 * Configuration class for setting up R2DBC with dynamic URL based on node ID.
 */
@Configuration
public class DatabaseConfig extends AbstractR2dbcConfiguration {

    private final NodeConfig nodeConfig;

    public DatabaseConfig(NodeConfig nodeConfig) {
        this.nodeConfig = nodeConfig;
    }

    @NonNull
    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        // Dynamically construct the database name based on node ID
        String dbName = "db_" + nodeConfig.getId();

        // Configure H2 ConnectionFactory with the dynamic database name
        return new H2ConnectionFactory(
                H2ConnectionConfiguration.builder()
                        .inMemory(dbName) // Each node gets its own in-memory DB
                        .property("DB_CLOSE_DELAY", "-1")
                        .property("DB_CLOSE_ON_EXIT", "FALSE")
                        .build()
        );
    }

    @Bean
//    @ConditionalOnProperty(name = "spring.r2dbc.initialization-mode", havingValue = "always")
    public ConnectionFactoryInitializer initializer(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);

        // Initialize schema
        CompositeDatabasePopulator populate = new CompositeDatabasePopulator();
        populate.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
        initializer.setDatabasePopulator(populate);

        return initializer;
    }
}
