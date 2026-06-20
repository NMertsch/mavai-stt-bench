package org.mavai.sttbench.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.nio.file.Path;
import org.mavai.outcome.Outcome;

/**
 * Loads {@link Recipe} definitions from YAML files under {@code recipes/}.
 *
 * <p>A malformed or unreadable recipe file is an <em>expected failure</em>:
 * the benchmark should report it and carry on, not crash. So
 * {@link #load(Path)} returns an {@link Outcome} — {@code ok} with the parsed
 * recipe, or {@code fail} with a named parse/IO failure — rather than throwing.
 * Genuine programming defects (a {@code null} path) still throw.
 */
public final class RecipeLoader {

    private final ObjectMapper yaml = new ObjectMapper(new YAMLFactory());

    /**
     * Loads and parses a single recipe file.
     *
     * @param recipeFile path to a {@code .yml} recipe under {@code recipes/}
     * @return {@link Outcome#ok} with the parsed recipe, or
     *     {@link Outcome#fail} ({@code "recipe-parse-error"}) if the file
     *     cannot be read or parsed
     */
    public Outcome<Recipe> load(Path recipeFile) {
        if (recipeFile == null) {
            throw new IllegalArgumentException("recipeFile must not be null");
        }
        try {
            Recipe recipe = yaml.readValue(recipeFile.toFile(), Recipe.class);
            return Outcome.ok(recipe);
        } catch (IOException e) {
            return Outcome.fail(
                    "recipe-parse-error",
                    "Could not load recipe %s: %s".formatted(recipeFile, e.getMessage()));
        }
    }
}
