package org.mavai.sttbench.provider;

import java.nio.file.Path;
import java.util.Objects;

/**
 * A request to transcribe one audio clip.
 *
 * @param clipId         stable id of the source clip (e.g. {@code "shopping_001"})
 * @param recipeId       id of the recipe applied to produce this audio, or
 *                       {@code "clean"} for the unmodified corpus clip
 * @param audioPath      filesystem path to the (possibly transformed) audio file
 * @param languageTag    BCP-47 language tag of the spoken content (e.g. {@code "en-GB"})
 */
public record SttRequest(String clipId, String recipeId, Path audioPath, String languageTag) {

    public SttRequest {
        Objects.requireNonNull(clipId, "clipId");
        Objects.requireNonNull(recipeId, "recipeId");
        Objects.requireNonNull(audioPath, "audioPath");
        Objects.requireNonNull(languageTag, "languageTag");
    }
}
