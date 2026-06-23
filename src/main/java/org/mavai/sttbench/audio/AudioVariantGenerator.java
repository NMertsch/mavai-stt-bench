package org.mavai.sttbench.audio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mavai.outcome.Outcome;
import org.mavai.sttbench.recipe.AudioRecipe;
import org.mavai.sttbench.recipe.AudioRecipeLoader;
import org.mavai.sttbench.recipe.AudioRecipeStep;

/**
 * Derives the {@code corpus × recipe} matrix of audio variants: for each source
 * clip and each recipe, folds the recipe's steps over the decoded clip and
 * writes the result (plus a {@link Provenance} sidecar) under the output
 * directory as {@code <recipe-id>/<clip-id>__<recipe-id>.wav}.
 *
 * <p>Expected failures — an undecodable clip, a missing step parameter, an
 * unimplemented step type — are collected into the {@link Summary} and the run
 * continues; nothing here throws on an expected failure. So a corpus with the
 * bundled recipes produces {@code clean} and {@code telephone-bandwidth}
 * variants today and reports the {@code cafe-*} recipes as the steps left to
 * implement (see {@link AudioTransforms}).
 *
 * <p>This is the engine the {@code generateAudioVariants} Gradle task drives via
 * {@link #main}.
 */
public final class AudioVariantGenerator {

    private static final Logger LOG = LogManager.getLogger(AudioVariantGenerator.class);

    /**
     * Generates every {@code clip × recipe} variant under {@code outDir}.
     *
     * @param corpusAudioDir directory of source {@code .wav} clips
     * @param recipes        recipes to apply
     * @param outDir         output root for generated variants
     * @return a summary of what was written and what failed
     */
    public Summary generate(Path corpusAudioDir, List<AudioRecipe> recipes, Path outDir) {
        Objects.requireNonNull(recipes, "recipes");
        Objects.requireNonNull(outDir, "outDir");
        List<Path> clips = listWavs(corpusAudioDir);
        if (clips.isEmpty()) {
            LOG.warn("No .wav clips found under {} — nothing to generate.", corpusAudioDir);
        }
        int written = 0;
        List<String> failures = new ArrayList<>();
        for (Path clip : clips) {
            for (AudioRecipe recipe : recipes) {
                Outcome<Path> result = generateOne(clip, recipe, outDir);
                if (result.isOk()) {
                    written++;
                    LOG.info("wrote {}", result.getOrThrow());
                } else {
                    String message = "%s × %s: %s".formatted(
                            fileName(clip), recipe.id(), failMessage(result));
                    failures.add(message);
                    LOG.warn(message);
                }
            }
        }
        return new Summary(written, failures.size(), List.copyOf(failures));
    }

    private Outcome<Path> generateOne(Path clip, AudioRecipe recipe, Path outDir) {
        byte[] inputBytes;
        try {
            inputBytes = Files.readAllBytes(clip);
        } catch (IOException e) {
            return Outcome.fail("audio-read-error", "Could not read %s: %s".formatted(clip, e.getMessage()));
        }

        Outcome<AudioClip> processed = WavIo.read(clip);
        for (AudioRecipeStep step : recipe.steps()) {
            processed = processed.flatMap(c -> AudioTransforms.forStep(step).flatMap(t -> t.apply(c)));
        }

        String clipId = stripWavSuffix(fileName(clip));
        Path variant = outDir.resolve(recipe.id()).resolve(clipId + "__" + recipe.id() + ".wav");
        return processed.flatMap(result -> WavIo.write(result, variant).flatMap(ignored -> {
            Outcome<Void> prov = Provenance.of(clipId, recipe, inputBytes).writeSidecar(variant);
            if (prov.isFail()) {
                LOG.warn("provenance: {}", failMessage(prov));
            }
            return Outcome.ok(variant);
        }));
    }

    private static List<Path> listWavs(Path dir) {
        if (dir == null || !Files.isDirectory(dir)) {
            return List.of();
        }
        try (Stream<Path> entries = Files.list(dir)) {
            return entries
                    .filter(p -> p.toString().toLowerCase(Locale.ROOT).endsWith(".wav"))
                    .sorted()
                    .toList();
        } catch (IOException e) {
            LOG.warn("Could not list {}: {}", dir, e.getMessage());
            return List.of();
        }
    }

    private static String fileName(Path p) {
        return p.getFileName().toString();
    }

    private static String stripWavSuffix(String name) {
        return name.regionMatches(true, name.length() - 4, ".wav", 0, 4)
                ? name.substring(0, name.length() - 4)
                : name;
    }

    private static String failMessage(Outcome<?> outcome) {
        return outcome instanceof Outcome.Fail<?> fail ? fail.failure().message() : "ok";
    }

    /**
     * Outcome of a generation run.
     *
     * @param written  number of variants written
     * @param failed   number of {@code clip × recipe} pairs that failed
     * @param failures human-readable failure messages, one per failed pair
     */
    public record Summary(int written, int failed, List<String> failures) {
    }

    /**
     * CLI entry point for the {@code generateAudioVariants} Gradle task.
     *
     * @param args {@code <corpusAudioDir> <recipesDir> <outDir>}
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            LOG.error("usage: AudioVariantGenerator <corpusAudioDir> <recipesDir> <outDir>");
            return;
        }
        Path corpusAudioDir = Path.of(args[0]);
        Path recipesDir = Path.of(args[1]);
        Path outDir = Path.of(args[2]);

        List<AudioRecipe> recipes = loadRecipes(recipesDir);
        if (recipes.isEmpty()) {
            LOG.warn("No recipes loaded from {}.", recipesDir);
        }
        Summary summary = new AudioVariantGenerator().generate(corpusAudioDir, recipes, outDir);
        LOG.info("generateAudioVariants: {} written, {} failed.", summary.written(), summary.failed());
        summary.failures().forEach(LOG::warn);
    }

    private static List<AudioRecipe> loadRecipes(Path recipesDir) {
        if (!Files.isDirectory(recipesDir)) {
            return List.of();
        }
        AudioRecipeLoader loader = new AudioRecipeLoader();
        List<AudioRecipe> recipes = new ArrayList<>();
        try (Stream<Path> entries = Files.list(recipesDir)) {
            entries
                    .filter(p -> p.toString().toLowerCase(Locale.ROOT).endsWith(".yml"))
                    .sorted()
                    .forEach(file -> {
                        Outcome<AudioRecipe> loaded = loader.load(file);
                        if (loaded.isOk()) {
                            recipes.add(loaded.getOrThrow());
                        } else {
                            LOG.warn("skipping {}: {}", file, failMessage(loaded));
                        }
                    });
        } catch (IOException e) {
            LOG.warn("Could not list recipes under {}: {}", recipesDir, e.getMessage());
        }
        return recipes;
    }
}
