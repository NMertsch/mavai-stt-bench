# Recipes

A **recipe** is a reusable, declarative audio-transformation specification.
The benchmark applies a recipe across every clip in the corpus to produce the
`Corpus × Recipe` matrix of generated audio.

A recipe **never references source audio** — that is what makes it reusable
across corpora, domains, languages, and dialects. It describes *what
transformation to apply*, not *what to apply it to*.

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

- `id` — stable identifier; also the generated-audio sub-directory name.
- `description` — human-readable summary.
- `steps` — ordered transformation steps; each has a `type` plus
  step-specific parameters.

## Bundled recipes

| File                       | Simulates                                  |
|----------------------------|--------------------------------------------|
| `clean.yml`                | pass-through control (no transformation)   |
| `telephone-bandwidth.yml`  | low-fidelity analogue telephone channel    |
| `cafe-light.yml`           | quiet café, high signal-to-noise ratio     |
| `cafe-heavy.yml`           | busy café, low signal-to-noise ratio       |

The recipe **model** (`org.mavai.sttbench.recipe.Recipe`,
`RecipeStep`, `RecipeLoader`) parses these files. The **engine** that executes
the steps is an intentional gap — see the repository README, *How to add a
recipe*, and the `generateAudioVariants` Gradle task.
