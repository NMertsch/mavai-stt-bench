# Changelog

All notable changes to this project are documented here. The format is based
on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project
adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Initial benchmarking scaffold for evaluating Speech-to-Text (STT) services
  with PUnit.
- Repository layout: corpus and recipes under `src/main/resources/`
  (`corpus/`, `recipes/`); `providers/`, `contracts/`, `results/` at the root.
  Generated audio defaults to `build/generated-audio/` and reports to
  `build/reports/stt/` (ephemeral, per Gradle convention; generated-audio
  redirectable via `-Psttbench.generatedAudioDir`).
- Recipe model (`Recipe`, `RecipeStep`, `RecipeLoader`) and four bundled
  recipes: `clean`, `telephone-bandwidth`, `cafe-light`, `cafe-heavy`.
- Provider abstraction (`SttProvider`, `SttRequest`, `SttResponse`) with a
  `noop` provider that needs no API keys or network.
- Skeletal evaluation metrics: transcript normalisation, Word Error Rate,
  Character Error Rate, Token F1.
- Predefined PUnit STT service contract (`SttServiceContract`) with non-empty,
  normalised-exact-match, WER, CER, and Token F1 criteria.
- Skeletal reporting layer consolidating PUnit explore results into HTML and
  Markdown comparison reports.
- Gradle build (Kotlin DSL, Java 21, JUnit 6) with placeholder workflow tasks
  `generateAudioVariants`, `runSttBenchmark`, `generateSttReport`.

### Notes

- Implementation is intentionally incomplete — designed as a Hackergarten
  starting point with obvious extension points.
- Validating PUnit compatibility with JUnit 6 is a stated project goal.
