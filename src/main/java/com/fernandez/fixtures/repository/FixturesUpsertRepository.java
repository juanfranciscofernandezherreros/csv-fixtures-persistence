package com.fernandez.fixtures.repository;

import com.fernandez.fixtures.entity.Fixtures;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

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
        jdbcTemplate.update(UPSERT_SQL, ps -> bind(ps, fixture));
    }

    public void upsertBatch(List<Fixtures> fixtures) {
        if (fixtures.isEmpty()) {
            return;
        }
        jdbcTemplate.batchUpdate(
                UPSERT_SQL,
                fixtures,
                fixtures.size(),
                (ps, fixture) -> bind(ps, fixture));
    }

    private void bind(PreparedStatement ps, Fixtures fixture) throws SQLException {
        ps.setString(1, fixture.getMatchId());
        ps.setString(2, fixture.getCountry());
        ps.setString(3, fixture.getCompetition());
        ps.setString(4, fixture.getEventTime());
        ps.setString(5, fixture.getHomeTeam());
        ps.setString(6, fixture.getAwayTeam());
    }
}
