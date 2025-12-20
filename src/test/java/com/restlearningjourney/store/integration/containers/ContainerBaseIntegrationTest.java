package com.restlearningjourney.store.integration.containers;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test-container") // use application-test-container.yml if overrides are needed
public abstract class ContainerBaseIntegrationTest {

    @Container
    public static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.44")
            .withDatabaseName("integration_test_db")
            .withUsername("root")
            .withPassword("test")
            .withStartupTimeout(Duration.ofMinutes(5));;

    @DynamicPropertySource
    static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);

        // To allow  flyway cleaning and running
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.clean-disabled", () -> "false");

    }

    @Autowired
    private Flyway flyway;

    @BeforeEach
    void cleanAndMigrate() {
        //cleans the DB and apply migrations
        flyway.clean();
        flyway.migrate();
    }
}
