package com.fernandez.fixtures.service;

import com.fernandez.fixtures.avro.FixtureValue;
import com.fernandez.fixtures.mapper.FixtureMapper;
import com.fernandez.fixtures.repository.FixturesUpsertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FixturesPersistenceService {
    private final FixturesUpsertRepository fixtures;
    private final FixtureMapper mapper;

    public FixturesPersistenceService(FixturesUpsertRepository fixtures, FixtureMapper mapper) {
        this.fixtures = fixtures;
        this.mapper = mapper;
    }

    @Transactional
    public void persist(FixtureValue value) {
        fixtures.upsert(mapper.toEntity(value));
    }

    @Transactional
    public void persistBatch(List<FixtureValue> values) {
        if (values == null || values.isEmpty()) {
            return;
        }
        fixtures.upsertBatch(values.stream().map(mapper::toEntity).toList());
    }
}
