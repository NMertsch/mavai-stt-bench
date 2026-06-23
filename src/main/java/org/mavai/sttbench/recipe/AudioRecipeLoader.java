package org.mavai.sttbench.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.nio.file.Path;
import org.mavai.outcome.Outcome;

/**
 * Loads {@link AudioRecipe} definitions from YAML files under
 * {@code src/main/resources/recipes/}.
 *
 * <p>A malformed or unreadable recipe file is an <em>expected failure</em>:
 * the benchmark should report it and carry on, not crash. So
 * {@link #load(Path)} returns an {@link Outcome} — {@code ok} with the parsed
 * recipe, or {@code fail} with a named parse/IO failure — rather than throwing.
 * Genuine programming defects (a {@code null} path) still throw.
 */
public final class AudioRecipeLoader {

    private final ObjectMapper yaml = new ObjectMapper(new YAMLFactory());

    /**
     * Loads and parses a single recipe file.
     *
     * @param recipeFile path to a {@code .yml} recipe (e.g. under
     *     {@code src/main/resources/recipes/}, or its classpath copy)
     * @return {@link Outcome#ok} with the parsed recipe, or
     *     {@link Outcome#fail} ({@code "recipe-parse-error"}) if the file
     *     cannot be read or parsed
     */
    public Outcome<AudioRecipe> load(Path recipeFile) {
        if (recipeFile == null) {
            throw new IllegalArgumentException("recipeFile must not be null");
        }
        try {
            AudioRecipe recipe = yaml.readValue(recipeFile.toFile(), AudioRecipe.class);
            return Outcome.ok(recipe);
        } catch (IOException e) {
            return Outcome.fail(
                    "recipe-parse-error",
                    "Could not load recipe %s: %s".formatted(recipeFile, e.getMessage()));
        }
    }
}
