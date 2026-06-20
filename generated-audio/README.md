# Generated audio

Derived audio produced by applying a recipe to the corpus:

```
Corpus × Recipe
```

Layout — one sub-directory per recipe, files suffixed with the recipe id:

```
generated-audio/telephone-bandwidth/
    shopping_001__telephone-bandwidth.wav
```

This content is **gitignored** — it is reproducible from the corpus and the
recipes by running:

```bash
./gradlew generateAudioVariants
```

Only this README is tracked. Do not commit generated audio.
