![version](https://img.shields.io/badge/version-1.1.1-blue)
# csv-fixtures-persistence

```text
fixtures.parsed -> csv-fixtures-persistence -> PostgreSQL fixtures
```

Consume una fila Avro por fixture y la guarda en PostgreSQL usando la clave primaria `(match_id, country, competition)`.

La responsabilidad termina en PostgreSQL: no publica topics de salida y no mantiene tablas de estado adicionales.

El PR se fusiona automáticamente a `main` cuando pasan los checks y después se elimina la rama origen.


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
