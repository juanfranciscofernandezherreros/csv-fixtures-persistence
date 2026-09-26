# Changelog

## 1.2.0 - 2026-09-26

- [minor] KAN-121 define `(match_id, country, competition)` como clave natural de idempotencia para FIXTURES.
- [minor] Sustituye `saveAndFlush()` por un upsert PostgreSQL atómico con `ON CONFLICT (match_id, country, competition) DO UPDATE`.
- [minor] Documenta la política current-state y la ausencia actual de `sourceEventId` en el contrato Avro compartido.
- [minor] Añade tests de integración para redelivery, reimportación y redelivery concurrente.

## 1.1.1 - 2026-09-26

- [patch] KAN-105 captura errores de deserialización Avro mediante `ErrorHandlingDeserializer`.
- [patch] Permite publicar en DLT objetos Avro y bytes crudos con `DelegatingByTypeSerializer`.
- [patch] Deja que Kafka elija la partición DLT y hace visible cualquier fallo de publicación en la DLT.
- [patch] Añade cobertura de deserialización fallida → DLT conservando los bytes originales.


## 1.1.0 - 2026-09-26

- [minor] KAN-105 aplica la estrategia común de errores Kafka de KAN-18.
- [minor] Clasifica errores de datos/integridad como non-retryable y fallos transitorios de base de datos como retryable.
- [minor] Configura retries/backoff y DLT `fixtures.parsed.DLT`.
- [minor] Añade tests de clasificación de error permanente y transitorio.


## 1.0.5 - 2026-09-25

- [patch] KAN-79 sustituye FixtureKey/FixtureValue locales por `basketball-event-contracts:1.0.2`.
- [patch] Elimina generación Avro local y autentica GitHub Packages en CI.
- [patch] Mantiene sin cambios el modelo de persistencia PostgreSQL.


## 1.0.4 - 2026-09-25

- [patch] Refuerza AGENTS.md con lectura obligatoria por tarea, autonomía y prohibición absoluta de escrituras directas en main.

## 1.0.3

- [patch] Estandariza la automatización del repositorio con el flujo autónomo de csv-results-parser.

## 1.0.2 - 2026-09-25

- [patch] Añade eliminación automática de la rama origen después de mergear una Pull Request en `main`.

## 1.0.1 - 2026-09-25

- [patch] Exige confirmar rama y nivel SemVer antes de cualquier cambio.

## 1.0.0 - 2026-09-24
- Separa la persistencia de FIXTURES.
- Consume `fixtures.parsed` y persiste únicamente en PostgreSQL.
- Conserva la PK `(match_id,country,competition)`.
- Añade auto-merge tras checks y borrado de rama.
