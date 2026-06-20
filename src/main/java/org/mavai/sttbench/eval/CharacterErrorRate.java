package org.mavai.sttbench.eval;

/**
 * Character Error Rate (CER): character-level edit distance between reference
 * and hypothesis, divided by the number of reference characters.
 *
 * <p>CER complements {@link WordErrorRate}: it is more forgiving of small
 * spelling slips and more informative for languages without clear word
 * boundaries. Lower is better.
 *
 * <p><strong>Skeletal.</strong> Operates on the normalised string with
 * spaces retained. As with WER, the Levenshtein routine is unoptimised and
 * carries no S/I/D breakdown.
 */
public final class CharacterErrorRate {

    private CharacterErrorRate() {
    }

    /**
     * Computes CER of {@code hypothesis} against {@code reference}.
     *
     * @param reference  the ground-truth transcript
     * @param hypothesis the provider's transcript
     * @return the CER in {@code [0, ∞)}; {@code 0.0} for a perfect match,
     *     and {@code 0.0} when both normalise to empty
     */
    public static double compute(String reference, String hypothesis) {
        String ref = TranscriptNormaliser.normalise(reference);
        String hyp = TranscriptNormaliser.normalise(hypothesis);
        if (ref.isEmpty()) {
            return hyp.isEmpty() ? 0.0 : 1.0;
        }
        int distance = levenshtein(ref, hyp);
        return (double) distance / ref.length();
    }

    private static int levenshtein(String a, String b) {
        int[][] d = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) {
            d[i][0] = i;
        }
        for (int j = 0; j <= b.length(); j++) {
            d[0][j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                d[i][j] = Math.min(
                        Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1),
                        d[i - 1][j - 1] + cost);
            }
        }
        return d[a.length()][b.length()];
    }
}
