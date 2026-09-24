package com.fernandez.fixtures;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;
import java.sql.DriverManager;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class PostgreSqlMigrationIT {
 @Container static final PostgreSQLContainer<?> POSTGRES=new PostgreSQLContainer<>("postgres:15-alpine");
 @Test void migrationCreatesFixturesTable() throws Exception {
  Flyway.configure().dataSource(POSTGRES.getJdbcUrl(),POSTGRES.getUsername(),POSTGRES.getPassword())
   .locations("classpath:db/migration").baselineOnMigrate(true).baselineVersion("1").load().migrate();
  try(var c=DriverManager.getConnection(POSTGRES.getJdbcUrl(),POSTGRES.getUsername(),POSTGRES.getPassword());
      var s=c.prepareStatement("select count(*) from information_schema.tables where table_schema='public' and table_name='fixtures'");
      var r=s.executeQuery()){r.next();assertEquals(1,r.getInt(1));}
 }
}
