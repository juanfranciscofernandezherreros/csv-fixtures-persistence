package com.fernandez.fixtures.service;

import com.fernandez.fixtures.avro.FixtureValue;
import com.fernandez.fixtures.entity.Fixtures;
import com.fernandez.fixtures.mapper.FixtureMapper;
import com.fernandez.fixtures.repository.FixturesUpsertRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FixturesPersistenceServiceTest {

    @Test
    void upsertsFixtureAtomically() {
        var repository = mock(FixturesUpsertRepository.class);
        var mapper = mock(FixtureMapper.class);
        var value = mock(FixtureValue.class);
        var entity = new Fixtures();

        when(mapper.toEntity(value)).thenReturn(entity);

        new FixturesPersistenceService(repository, mapper).persist(value);

        verify(repository).upsert(entity);
    }
}
