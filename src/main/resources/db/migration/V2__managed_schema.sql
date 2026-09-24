CREATE TABLE IF NOT EXISTS fixtures (
    match_id VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    competition VARCHAR(255) NOT NULL,
    event_time VARCHAR(255),
    home_team VARCHAR(255),
    away_team VARCHAR(255),
    PRIMARY KEY(match_id,country,competition)
);
