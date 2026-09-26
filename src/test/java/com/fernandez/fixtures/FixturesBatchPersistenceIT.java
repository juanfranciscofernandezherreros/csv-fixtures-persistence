package com.fernandez.fixtures;

import com.fernandez.fixtures.avro.FixtureValue;
import com.fernandez.fixtures.entity.Fixtures;
import com.fernandez.fixtures.repository.FixturesRepository;
import com.fernandez.fixtures.repository.FixturesUpsertRepository;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(properties = {
        "spring.kafka.listener.auto-startup=false",
        "spring.kafka.bootstrap-servers=localhost:9092",
        "spring.kafka.properties.schema.registry.url=mock://fixtures-batch-it"
})
class FixturesBatchPersistenceIT {

    private static final int BENCHMARK_ROWS = 1_000;

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("fixtures_batch")
                    .withUsername("fixtures")
                    .withPassword("fixtures");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired FixturesPersistenceService service;
    @Autowired FixturesRepository repository;
    @Autowired FixturesUpsertRepository upserts;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void persistsKafkaBatchWithCurrentStateUpsertSemantics() {
        service.persistBatch(List.of(
                fixture("m1", "20:00", "Home", "Away"),
                fixture("m2", "21:00", "Home 2", "Away 2"),
                fixture("m1", "20:30", "Home corrected", "Away")));

        assertEquals(2, repository.count());
        var corrected = repository.findAll().stream()
                .filter(row -> "m1".equals(row.getMatchId()))
                .findFirst()
                .orElseThrow();
        assertEquals("20:30", corrected.getEventTime());
        assertEquals("Home corrected", corrected.getHomeTeam());
    }

    @Test
    void measuresSequentialVersusJdbcBatchThroughput() {
        List<Fixtures> fixtures = new ArrayList<>(BENCHMARK_ROWS);
        for (int i = 0; i < BENCHMARK_ROWS; i++) {
            Fixtures row = new Fixtures();
            row.setMatchId("benchmark-" + i);
            row.setCountry("es");
            row.setCompetition("acb");
            row.setEventTime("20:00");
            row.setHomeTeam("Home " + i);
            row.setAwayTeam("Away " + i);
            fixtures.add(row);
        }

        long sequentialStart = System.nanoTime();
        fixtures.forEach(upserts::upsert);
        long sequentialNanos = System.nanoTime() - sequentialStart;
        assertEquals(BENCHMARK_ROWS, repository.count());

        repository.deleteAll();

        long batchStart = System.nanoTime();
        upserts.upsertBatch(fixtures);
        long batchNanos = System.nanoTime() - batchStart;
        assertEquals(BENCHMARK_ROWS, repository.count());

        double sequentialRowsPerSecond =
                BENCHMARK_ROWS / (sequentialNanos / 1_000_000_000.0);
        double batchRowsPerSecond =
                BENCHMARK_ROWS / (batchNanos / 1_000_000_000.0);

        System.out.printf(
                "KAN-40 throughput rows=%d sequential=%.0f rows/s batch=%.0f rows/s speedup=%.2fx%n",
                BENCHMARK_ROWS,
                sequentialRowsPerSecond,
                batchRowsPerSecond,
                batchRowsPerSecond / sequentialRowsPerSecond);

        assertTrue(batchNanos < sequentialNanos,
                "JDBC batch should reduce PostgreSQL round-trips");
    }

    private FixtureValue fixture(String matchId, String eventTime, String homeTeam, String awayTeam) {
        return FixtureValue.newBuilder()
                .setMatchId(matchId)
                .setCountry("es")
                .setCompetition("acb")
                .setEventTime(eventTime)
                .setHomeTeam(homeTeam)
                .setAwayTeam(awayTeam)
                .build();
    }
}
