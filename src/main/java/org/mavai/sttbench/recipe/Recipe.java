package org.mavai.sttbench.recipe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * A reusable audio-transformation specification, loaded from a {@code .yml}
 * file under {@code recipes/}.
 *
 * <p>A recipe is corpus-agnostic by design: it never references source audio.
 * The benchmark applies a recipe across every clip in a corpus, producing the
 * {@code Corpus × Recipe} matrix of generated audio. The same recipe is
 * therefore reusable across corpora, domains, languages, and dialects.
 *
 * @param id          stable recipe id, also the generated-audio sub-directory
 *                    name (e.g. {@code "telephone-bandwidth"})
 * @param description human-readable summary of what the recipe simulates
 * @param steps       ordered transformation steps applied to each clip
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Recipe(String id, String description, List<RecipeStep> steps) {

    public Recipe {
        steps = steps == null ? List.of() : List.copyOf(steps);
    }
}
