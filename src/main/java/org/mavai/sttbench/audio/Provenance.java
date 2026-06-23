package org.mavai.sttbench.audio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;
import org.mavai.outcome.Outcome;
import org.mavai.sttbench.recipe.AudioRecipe;
import org.mavai.sttbench.recipe.AudioRecipeStep;

/**
 * The audit record written beside each generated variant: enough to reproduce
 * and verify the artefact independently of where the audio is stored.
 *
 * <p>This is the project's documented audit hook — the audit-grade answer is
 * provenance recorded with the verdict, not the audio's location. The record is
 * intentionally small; richer provenance (tool/library versions per step,
 * recipe file hash, corpus revision) is a natural extension.
 *
 * @param clipId       source clip id
 * @param recipeId     recipe applied
 * @param inputSha256  SHA-256 of the source audio file bytes
 * @param steps        ordered step types applied
 * @param generatedBy  the generator and Java version
 * @param generatedAt  ISO-8601 instant of generation
 */
public record Provenance(
        String clipId,
        String recipeId,
        String inputSha256,
        List<String> steps,
        String generatedBy,
        String generatedAt) {

    private static final ObjectWriter WRITER = new ObjectMapper().writerWithDefaultPrettyPrinter();

    /**
     * Builds a provenance record for a generated variant.
     *
     * @param clipId     source clip id
     * @param recipe     the recipe applied
     * @param inputBytes the source audio file's raw bytes (hashed)
     * @return the provenance record
     */
    public static Provenance of(String clipId, AudioRecipe recipe, byte[] inputBytes) {
        List<String> steps = recipe.steps().stream().map(AudioRecipeStep::type).toList();
        return new Provenance(
                clipId,
                recipe.id(),
                sha256(inputBytes),
                steps,
                "mavai-stt-bench AudioVariantGenerator; Java " + System.getProperty("java.version"),
                Instant.now().toString());
    }

    /**
     * Writes this record as a {@code <variant>.provenance.json} sidecar.
     *
     * @param variantFile the generated variant the sidecar describes
     * @return {@code ok}, or {@code fail} ({@code "provenance-write-error"})
     */
    public Outcome<Void> writeSidecar(Path variantFile) {
        Path sidecar = variantFile.resolveSibling(variantFile.getFileName() + ".provenance.json");
        try {
            WRITER.writeValue(sidecar.toFile(), this);
            return Outcome.ok();
        } catch (IOException e) {
            return Outcome.fail("provenance-write-error",
                    "Could not write provenance %s: %s".formatted(sidecar, e.getMessage()));
        }
    }

    private static String sha256(byte[] bytes) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(bytes);
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append("%02x".formatted(b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is mandated by the platform; absence is a genuine defect.
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
