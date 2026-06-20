package org.mavai.sttbench.eval;

import java.util.Locale;

/**
 * Normalises transcripts to a canonical form before scoring.
 *
 * <p>STT evaluation compares a hypothesis transcript against a reference
 * transcript. Both must be normalised the same way so that scoring measures
 * transcription accuracy, not incidental formatting differences (casing,
 * punctuation, whitespace). The normaliser is the single place that
 * definition lives — every metric runs over its output.
 *
 * <p><strong>Skeletal.</strong> The current implementation lower-cases,
 * strips punctuation, and collapses whitespace. A Hackergarten session
 * should extend it toward the choices a real STT benchmark needs:
 * number-word handling ({@code "5"} vs {@code "five"}), contraction
 * expansion, locale-aware casing, and configurable rule sets per language
 * and dialect.
 */
public final class TranscriptNormaliser {

    private TranscriptNormaliser() {
    }

    /**
     * Returns the canonical form of a transcript.
     *
     * @param text the raw transcript (reference or hypothesis)
     * @return the normalised transcript; never {@code null}
     */
    public static String normalise(String text) {
        if (text == null) {
            return "";
        }
        // Skeletal: lower-case, drop punctuation, collapse whitespace.
        // TODO(hackergarten): number-word normalisation, contractions,
        // per-language rule sets.
        String lowered = text.toLowerCase(Locale.ROOT);
        String depunctuated = lowered.replaceAll("[\\p{Punct}]", " ");
        return depunctuated.trim().replaceAll("\\s+", " ");
    }
}
