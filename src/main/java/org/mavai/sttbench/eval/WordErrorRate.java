package org.mavai.sttbench.eval;

/**
 * Word Error Rate (WER): edit distance between reference and hypothesis word
 * sequences, divided by the number of reference words.
 *
 * <p>WER is the canonical STT accuracy metric. It counts word-level
 * substitutions, insertions, and deletions. Lower is better; {@code 0.0}
 * means a perfect match after normalisation.
 *
 * <p><strong>Not implemented yet — this is a Hackergarten task.</strong> The
 * behaviour you are implementing towards is pinned by the (currently
 * {@code @Disabled}) spec in {@code WordErrorRateTest}. Enable that test, then
 * make it green. A worked example of the static-util, normalise-then-score
 * shape lives in {@link TranscriptNormaliser} — copy its pattern.
 *
 * <p>The shape: tokenise both sides through {@link TranscriptNormaliser},
 * compute the word-level Levenshtein (edit) distance, and divide by the
 * reference word count. A simple full-matrix Levenshtein is fine to start; a
 * substitution / insertion / deletion breakdown is a natural follow-up.
 */
public final class WordErrorRate {

    private WordErrorRate() {
    }

    /**
     * Computes WER of {@code hypothesis} against {@code reference}.
     *
     * <p>Contract the spec test asserts: {@code 0.0} for a perfect match (after
     * normalisation), {@code 0.0} when both sides normalise to empty, and
     * {@code 1.0} when the reference is empty but the hypothesis is not. A
     * single word substitution, insertion, or deletion against an
     * {@code n}-word reference scores {@code 1.0 / n}.
     *
     * @param reference  the ground-truth transcript
     * @param hypothesis the provider's transcript
     * @return the WER in {@code [0, ∞)}; {@code 0.0} for a perfect match
     */
    public static double compute(String reference, String hypothesis) {
        throw new UnsupportedOperationException(
                "Hackergarten: implement Word Error Rate — see WordErrorRateTest for the spec");
    }
}
