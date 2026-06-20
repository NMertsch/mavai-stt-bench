# mavai-stt-bench

A forkable benchmarking scaffold for evaluating **Speech-to-Text (STT)**
services with [PUnit](https://github.com/mavai-org/punit), the JUnit-based
probabilistic testing framework from the [mavai](https://mavai.org) project
family.

> **Status: intentionally incomplete.** This repository is a Hackergarten
> starting point. It builds and runs end-to-end with a no-op provider, and it
> leaves clearly-marked extension points where the interesting work lives.

## 1. What this project is

A structured harness for asking *how well does an STT service transcribe my
audio, and how does it hold up as that audio degrades?* — answered with
statistical rigour via PUnit rather than a handful of ad-hoc spot checks.

You provide audio and reference transcripts (a **corpus**), declare audio
degradations (**recipes**), and plug in the STT services you want to compare
(**providers**). The harness generates degraded audio, runs a PUnit experiment
over each provider, and consolidates the results into an STT comparison report.

## 2. What this project is **not**

- **Not** a generic PUnit reporting subsystem. The reporting layer consumes
  PUnit explore results and emits an STT-specific comparison; for full PUnit
  reporting use `punit-report`.
- **Not** a semantic-similarity evaluator. STT systems are expected to
  *transcribe*, not paraphrase, so scoring is literal (WER, CER, Token F1) —
  never cosine or embedding similarity, and no manually-annotated
  keyword-retention criteria.
- **Not** an audio-DSP library. Recipes declare *what* transformation to
  apply; a Hackergarten task is to wire an audio engine that executes them.
- **Not** over-generalised. It is reusable across corpora, languages, and
  providers, but it stays opinionated about STT.

## 3. Intended fork-based workflow

1. **Fork** this repository.
2. **Drop in your corpus** — audio clips plus the reference transcripts that
   were read to record them.
3. **Choose or add recipes** — the audio degradations you care about.
4. **Add a provider** for each STT service you want to benchmark.
5. **Run** the pipeline and read the comparison report.
6. **Iterate** — tighten the contract thresholds, add recipes, add providers.

## 4. The pipeline: Audio → Recipe → STT → PUnit → Report

```
corpus/            recipes/              generated-audio/
 audio + transcripts  +  degradations  →   corpus × recipe
        │                                        │
        │                                        ▼
        │                              providers/  (SttProvider)
        │                                        │
        └──────────── reference ──────►  PUnit STT contract
                                                 │  explore
                                                 ▼
                                       results/explore/*.json
                                                 │  consolidate
                                                 ▼
                                  reports/  (HTML + Markdown)
```

Driven by three Gradle tasks:

```bash
./gradlew generateAudioVariants   # corpus × recipe  -> generated-audio/
./gradlew runSttBenchmark         # PUnit explore     -> results/explore/
./gradlew generateSttReport       # explore results   -> reports/
```

These tasks are **placeholders** in this scaffold — they describe what they
will do and point at the extension point. Implementing them is the headline
Hackergarten task.

## 5. How to add a corpus

The corpus is the ground truth. For each clip, add a recording and the exact
text that was read:

```
corpus/audio/shopping_001.wav        # recording
corpus/transcripts/shopping_001.txt  # text that was read (reference)
```

Match the stems (`shopping_001`). Casing and punctuation in the transcript do
not need a particular convention — both reference and hypothesis are normalised
before scoring (`org.mavai.sttbench.eval.TranscriptNormaliser`). See
[`corpus/README.md`](corpus/README.md).

## 6. How to add a recipe

A recipe is a reusable YAML transformation spec that **never references source
audio**:

```yaml
id: telephone-bandwidth
description: Simulates a low-fidelity analogue telephone channel.
steps:
  - type: highpass
    frequencyHz: 300
  - type: lowpass
    frequencyHz: 3400
  - type: resample
    sampleRateHz: 8000
```

Drop a `*.yml` file in [`recipes/`](recipes/). Four are bundled: `clean`,
`telephone-bandwidth`, `cafe-light`, `cafe-heavy`. The recipe **model** parses
your file (`org.mavai.sttbench.recipe.*`); the **engine** that executes the
steps is the gap `generateAudioVariants` fills.

## 7. How to add an STT provider

Implement `org.mavai.sttbench.provider.SttProvider`:

```java
public interface SttProvider {
    String id();
    Outcome<SttResponse> transcribe(SttRequest request);
}
```

Return an expected, service-level failure as data via
`Outcome.fail(name, message)`; reserve thrown exceptions for genuine defects
(a thrown exception aborts the whole run). Start from
`org.mavai.sttbench.provider.noop.NoopSttProvider`, which needs no API keys and
no network. **Never commit credentials** — `.env`, key files, and
`secrets.properties` are gitignored. See [`providers/README.md`](providers/README.md).

## 8. How PUnit fits in

The benchmark is, at heart, a PUnit experiment. The predefined service
contract `org.mavai.sttbench.contract.SttServiceContract` expresses STT
acceptance as PUnit pass-rate criteria:

- non-empty transcript,
- normalised exact match against the reference,
- WER below a threshold,
- CER below a threshold,
- Token F1 above a threshold.

PUnit runs this contract many times across clips and recipes and produces a
**statistical** verdict per provider — the point of the family: bringing
statistical rigour to the testing of stochastic services. Thresholds live on
the contract's factor record (`SttServiceContract.SttTuning`); varying them is
how an experiment sweeps acceptance strictness. Expected failures travel as
`org.mavai.outcome.Outcome` values, per the mavai Java convention.

## 9. JUnit 6 compatibility validation

This project **explicitly targets JUnit 6** (pinned in
[`gradle/libs.versions.toml`](gradle/libs.versions.toml)), where sibling mavai
projects still use the JUnit 5 line.

> Validation of PUnit compatibility with JUnit 6 is an intended project goal.

Exercising PUnit on top of JUnit 6 — and surfacing any incompatibility — is one
of the Hackergarten objectives. The test suite is the smallest evidence that
the PUnit + JUnit 6 stack compiles and runs here.

## Building

```bash
./gradlew clean build
```

Requires a JDK 21 toolchain. The Gradle wrapper is committed; no local Gradle
install is needed.

## Licence

Apache License 2.0 — see [LICENSE](LICENSE) and [NOTICE](NOTICE).
Contributions are accepted under the DCO; see [CONTRIBUTING.md](CONTRIBUTING.md).
