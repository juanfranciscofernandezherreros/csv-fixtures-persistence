package com.fernandez.fixtures.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Objects;

@Data
public class FixturesId implements Serializable {
    private String matchId;
    private String country;
    private String competition;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FixturesId that)) return false;
        return Objects.equals(matchId, that.matchId)
                && Objects.equals(country, that.country)
                && Objects.equals(competition, that.competition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchId, country, competition);
    }
}
