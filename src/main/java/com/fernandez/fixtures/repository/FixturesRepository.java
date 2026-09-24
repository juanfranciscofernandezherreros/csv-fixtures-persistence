package com.fernandez.fixtures.repository;
import com.fernandez.fixtures.entity.Fixtures;
import com.fernandez.fixtures.entity.FixturesId;
import org.springframework.data.jpa.repository.JpaRepository;
public interface FixturesRepository extends JpaRepository<Fixtures,FixturesId> {}
