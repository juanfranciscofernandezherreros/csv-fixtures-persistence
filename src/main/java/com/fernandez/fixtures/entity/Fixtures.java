package com.fernandez.fixtures.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="fixtures")
@IdClass(FixturesId.class)
@Data
public class Fixtures {
    @Id @Column(name="match_id",nullable=false) private String matchId;
    @Id @Column(nullable=false) private String country;
    @Id @Column(nullable=false) private String competition;
    @Column(name="event_time") private String eventTime;
    @Column(name="home_team") private String homeTeam;
    @Column(name="away_team") private String awayTeam;
}
