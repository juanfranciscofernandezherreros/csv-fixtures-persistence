package com.fernandez.fixtures.repository;

import com.fernandez.fixtures.entity.Fixtures;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FixturesUpsertRepository {

    private static final String UPSERT_SQL = """
            INSERT INTO fixtures (
                match_id, country, competition, event_time, home_team, away_team
            ) VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (match_id, country, competition) DO UPDATE SET
                event_time = EXCLUDED.event_time,
                home_team = EXCLUDED.home_team,
                away_team = EXCLUDED.away_team
            """;

    private final JdbcTemplate jdbcTemplate;

    public FixturesUpsertRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void upsert(Fixtures fixture) {
        jdbcTemplate.update(
                UPSERT_SQL,
                fixture.getMatchId(),
                fixture.getCountry(),
                fixture.getCompetition(),
                fixture.getEventTime(),
                fixture.getHomeTeam(),
                fixture.getAwayTeam()
        );
    }
}
