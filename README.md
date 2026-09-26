![version](https://img.shields.io/badge/version-1.2.0-blue)
# csv-fixtures-persistence

```text
fixtures.parsed -> csv-fixtures-persistence -> PostgreSQL fixtures
```

Consume una fila Avro por fixture y la guarda en PostgreSQL.

La responsabilidad termina en PostgreSQL: no publica topics de salida y no mantiene tablas de estado adicionales.

El PR se fusiona automáticamente a `main` cuando pasan los checks y después se elimina la rama origen.

## Política de idempotencia

KAN-121 aplica la estrategia común de KAN-19 a FIXTURES.

La identidad de negocio es la clave natural compuesta:

```text
(match_id, country, competition)
```

PostgreSQL la protege mediante la primary key de `fixtures`. La escritura se realiza con una única operación atómica:

```sql
INSERT ...
ON CONFLICT (match_id, country, competition)
DO UPDATE ...
```

La política es **current-state upsert**:

- un redelivery idéntico conserva una única fila;
- una reimportación de la misma clave natural actualiza `event_time`, `home_team` y `away_team`;
- no existe una ventana `read-then-write`;
- redeliveries concurrentes del mismo evento convergen en el mismo estado.

El contrato Avro compartido `FixtureValue` no contiene actualmente `sourceEventId`, por lo que FIXTURES no puede persistir trazabilidad de reimportación por ese identificador sin evolucionar primero el contrato. KAN-121 no inventa ese dato ni modifica el contrato compartido fuera de su alcance.

## Contratos Avro compartidos

`FixtureKey` y `FixtureValue` se consumen desde `com.fernandez.basketball:basketball-event-contracts:1.0.2`. Este repositorio ya no mantiene copias locales de esos schemas.

## Estrategia de errores Kafka

KAN-105 aplica la política común de KAN-18 al consumo de `fixtures.parsed`.

- errores de datos o integridad: non-retryable;
- fallos transitorios de PostgreSQL: retryable;
- mensajes agotados: `fixtures.parsed.DLT`;
- `KAFKA_RETRY_MAX_ATTEMPTS`: intentos totales, default `3`;
- `KAFKA_RETRY_BACKOFF_MS`: backoff fijo, default `1000`;
- `KAFKA_FIXTURES_PERSISTENCE_DLT_TOPIC`: permite cambiar el topic DLT.

La publicación DLT conserva el registro original y los headers de diagnóstico generados por Spring Kafka.

### Deserialización y DLT

Los deserializadores Avro están envueltos con `ErrorHandlingDeserializer`, por lo que un payload corrupto o incompatible entra en el flujo normal de recuperación. La DLT `fixtures.parsed.DLT` acepta objetos Avro y `byte[]` originales, conserva los headers de diagnóstico, deja que Kafka seleccione una partición válida y propaga cualquier fallo de publicación en la propia DLT.

## Validación

La suite rápida se ejecuta con:

```bash
mvn -B test
```

Los tests de integración de idempotencia con PostgreSQL real se ejecutan con:

```bash
mvn -B verify -Pintegration
```

Cubren redelivery, reimportación y redelivery concurrente.
