# Explore results

PUnit **explore** result files land here, conventionally organised by provider
and recipe, e.g.:

```
results/explore/<provider-id>/<recipe-id>.json
```

The reporting layer (`org.mavai.sttbench.report`) reads these and consolidates
them into an STT comparison report under `build/reports/stt/` (ephemeral,
derived output per Gradle convention — not tracked).

This content is gitignored; only this README is tracked.
