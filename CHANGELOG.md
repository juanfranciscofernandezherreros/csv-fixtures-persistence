# Changelog

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
