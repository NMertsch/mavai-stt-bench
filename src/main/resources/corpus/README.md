# Corpus

The corpus is the **ground truth** for the benchmark: pairs of an audio
recording and the exact text that was read to create it.

```
src/main/resources/corpus/audio/shopping_001.wav        # the recording
src/main/resources/corpus/transcripts/shopping_001.txt  # the text that was read
```

The transcript file shares the clip's stem and holds the reference text the
provider's output is scored against (after normalisation). One clip → one
transcript.

This directory ships empty (placeholders only). Drop your own clips and
transcripts in — see the repository README, *How to add a corpus*.

- [`audio/`](audio/) — source recordings (`*.wav`).
- [`transcripts/`](transcripts/) — reference transcripts (`*.txt`).

Source audio under `audio/` is committed (it is the input the benchmark
depends on) and ships as a classpath resource. Do not put **generated** audio
here — that lands under `build/generated-audio/` (ephemeral, per Gradle
convention; see the repository README).
