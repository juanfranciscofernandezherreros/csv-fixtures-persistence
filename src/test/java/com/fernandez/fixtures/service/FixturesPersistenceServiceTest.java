package com.fernandez.fixtures.service;

import com.fernandez.fixtures.avro.FixtureValue;
import com.fernandez.fixtures.entity.Fixtures;
import com.fernandez.fixtures.mapper.FixtureMapper;
import com.fernandez.fixtures.repository.FixturesRepository;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

class FixturesPersistenceServiceTest {
 @Test void persistsFixture(){
  var repository=mock(FixturesRepository.class);
  var mapper=mock(FixtureMapper.class);
  var value=mock(FixtureValue.class);
  var entity=new Fixtures();
  when(mapper.toEntity(value)).thenReturn(entity);
  new FixturesPersistenceService(repository,mapper).persist(value);
  verify(repository).saveAndFlush(entity);
 }
}
