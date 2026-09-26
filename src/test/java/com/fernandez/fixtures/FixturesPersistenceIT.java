package com.fernandez.fixtures;

import com.fernandez.fixtures.avro.FixtureValue;
import com.fernandez.fixtures.entity.FixturesId;
import com.fernandez.fixtures.repository.FixturesRepository;
import com.fernandez.fixtures.service.FixturesPersistenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(properties = {
        "spring.kafka.listener.auto-startup=false",
        "spring.kafka.bootstrap-servers=localhost:9092",
        "spring.kafka.properties.schema.registry.url=mock://fixtures-persistence-it"
})
class FixturesPersistenceIT {

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("fixtures")
                    .withUsername("fixtures")
                    .withPassword("fixtures");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    FixturesPersistenceService service;

    @Autowired
    FixturesRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void redeliveryKeepsSingleFixture() {
        FixtureValue event = fixture("20:00", "Home", "Away");

        service.persist(event);
        service.persist(event);

        assertEquals(1, repository.count());
        var saved = repository.findById(id()).orElseThrow();
        assertEquals("20:00", saved.getEventTime());
        assertEquals("Home", saved.getHomeTeam());
    }

    @Test
    void reimportUpdatesCurrentFixtureState() {
        service.persist(fixture("20:00", "Home", "Away"));
        service.persist(fixture("20:30", "Home corrected", "Away"));

        assertEquals(1, repository.count());
        var saved = repository.findById(id()).orElseThrow();
        assertEquals("20:30", saved.getEventTime());
        assertEquals("Home corrected", saved.getHomeTeam());
    }

    @Test
    void concurrentRedeliveryKeepsSingleFixture() throws Exception {
        FixtureValue event = fixture("20:00", "Home", "Away");
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<?> first = executor.submit(() -> persistAfter(start, event));
            Future<?> second = executor.submit(() -> persistAfter(start, event));

            start.countDown();
            first.get();
            second.get();

            assertEquals(1, repository.count());
            var saved = repository.findById(id()).orElseThrow();
            assertEquals("20:00", saved.getEventTime());
            assertEquals("Home", saved.getHomeTeam());
        } finally {
            executor.shutdownNow();
        }
    }

    private void persistAfter(CountDownLatch start, FixtureValue event) {
        try {
            start.await();
            service.persist(event);
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(interrupted);
        }
    }

    private FixtureValue fixture(String eventTime, String homeTeam, String awayTeam) {
        return FixtureValue.newBuilder()
                .setMatchId("m1")
                .setCountry("es")
                .setCompetition("acb")
                .setEventTime(eventTime)
                .setHomeTeam(homeTeam)
                .setAwayTeam(awayTeam)
                .build();
    }

    private FixturesId id() {
        FixturesId id = new FixturesId();
        id.setMatchId("m1");
        id.setCountry("es");
        id.setCompetition("acb");
        return id;
    }
}
