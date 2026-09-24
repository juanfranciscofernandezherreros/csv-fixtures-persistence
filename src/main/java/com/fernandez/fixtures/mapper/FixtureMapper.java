package com.fernandez.fixtures.mapper;

import com.fernandez.fixtures.avro.FixtureValue;
import com.fernandez.fixtures.entity.Fixtures;
import org.springframework.stereotype.Component;

@Component
public class FixtureMapper {
    public Fixtures toEntity(FixtureValue value) {
        Fixtures entity = new Fixtures();
        entity.setMatchId(value.getMatchId());
        entity.setCountry(value.getCountry());
        entity.setCompetition(value.getCompetition());
        entity.setEventTime(value.getEventTime());
        entity.setHomeTeam(value.getHomeTeam());
        entity.setAwayTeam(value.getAwayTeam());
        return entity;
    }
}
