package com.restlearningjourney.store.integration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // load application-test.yml
public abstract class BaseIntegrationTest {
    @Autowired
    private Flyway flyway;

    @BeforeEach
    void cleanAndMigrate() {
        //cleans the DB and apply migrations
        flyway.clean();
        flyway.migrate();
    }
}