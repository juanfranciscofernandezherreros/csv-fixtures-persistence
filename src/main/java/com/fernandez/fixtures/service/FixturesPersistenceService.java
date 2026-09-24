package com.fernandez.fixtures.service;

import com.fernandez.fixtures.avro.FixtureValue;
import com.fernandez.fixtures.mapper.FixtureMapper;
import com.fernandez.fixtures.repository.FixturesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FixturesPersistenceService {
    private final FixturesRepository fixtures;
    private final FixtureMapper mapper;

    public FixturesPersistenceService(FixturesRepository fixtures, FixtureMapper mapper) {
        this.fixtures = fixtures;
        this.mapper = mapper;
    }

    @Transactional
    public void persist(FixtureValue value) {
        fixtures.saveAndFlush(mapper.toEntity(value));
    }
}
