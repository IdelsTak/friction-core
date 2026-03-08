  # friction-core

  Core domain + application logic for Friction.

  ## Scope
  - Aggregates, entities, value objects
  - Domain events
  - RxJava3 pipelines
  - SPI contracts for sources and read models

  ## Docs
  See [docs](docs/) full architecture and design notes.

  ## Published Artifacts
  GitHub Packages publish includes:
  - Standard library jar: `friction-core-<version>.jar`
  - Non-shaded runtime dependencies copied at package time to:
    - `target/libs/*.jar`
