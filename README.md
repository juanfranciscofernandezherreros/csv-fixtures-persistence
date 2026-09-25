![version](https://img.shields.io/badge/version-1.0.3-blue)
# csv-fixtures-persistence

```text
fixtures.parsed -> csv-fixtures-persistence -> PostgreSQL fixtures
```

Consume una fila Avro por fixture y la guarda en PostgreSQL usando la clave primaria `(match_id, country, competition)`.

La responsabilidad termina en PostgreSQL: no publica topics de salida y no mantiene tablas de estado adicionales.

El PR se fusiona automáticamente a `main` cuando pasan los checks y después se elimina la rama origen.
