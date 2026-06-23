package org.mavai.sttbench.eval;

/**
 * Character Error Rate (CER): character-level edit distance between reference
 * and hypothesis, divided by the number of reference characters.
 *
 * <p>CER complements {@link WordErrorRate}: it is more forgiving of small
 * spelling slips and more informative for languages without clear word
 * boundaries. Lower is better.
 *
 * <p><strong>Not implemented yet — this is a Hackergarten task.</strong> The
 * behaviour you are implementing towards is pinned by the (currently
 * {@code @Disabled}) spec in {@code CharacterErrorRateTest}. Enable that test,
 * then make it green. It is the character-level twin of {@link WordErrorRate}:
 * normalise both sides, compute Levenshtein over the characters of the
 * normalised string (spaces retained), and divide by the reference length.
 */
public final class CharacterErrorRate {

    private CharacterErrorRate() {
    }

    /**
     * Computes CER of {@code hypothesis} against {@code reference}.
     *
     * <p>Contract the spec test asserts: {@code 0.0} for a perfect match (after
     * normalisation), {@code 0.0} when both sides normalise to empty, and
     * {@code 1.0} when the reference is empty but the hypothesis is not. A
     * single character edit against an {@code n}-character reference scores
     * {@code 1.0 / n}.
     *
     * @param reference  the ground-truth transcript
     * @param hypothesis the provider's transcript
     * @return the CER in {@code [0, ∞)}; {@code 0.0} for a perfect match
     */
    public static double compute(String reference, String hypothesis) {
        throw new UnsupportedOperationException(
                "Hackergarten: implement Character Error Rate — see CharacterErrorRateTest for the spec");
    }
}
