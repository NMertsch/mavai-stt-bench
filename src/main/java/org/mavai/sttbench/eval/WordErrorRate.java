package org.mavai.sttbench.eval;

import java.util.List;

/**
 * Word Error Rate (WER): edit distance between reference and hypothesis word
 * sequences, divided by the number of reference words.
 *
 * <p>WER is the canonical STT accuracy metric. It counts word-level
 * substitutions, insertions, and deletions. Lower is better; {@code 0.0}
 * means a perfect match after normalisation.
 *
 * <p><strong>Skeletal.</strong> The Levenshtein distance below is correct
 * but unoptimised, and the metric does not yet break the score down into
 * substitution / insertion / deletion components (which a real STT report
 * wants). Both are natural Hackergarten extensions. Inputs are normalised
 * via {@link TranscriptNormaliser} before scoring.
 */
public final class WordErrorRate {

    private WordErrorRate() {
    }

    /**
     * Computes WER of {@code hypothesis} against {@code reference}.
     *
     * @param reference  the ground-truth transcript
     * @param hypothesis the provider's transcript
     * @return the WER in {@code [0, ∞)}; {@code 0.0} for a perfect match,
     *     and {@code 0.0} when both normalise to empty
     */
    public static double compute(String reference, String hypothesis) {
        List<String> ref = tokenise(reference);
        List<String> hyp = tokenise(hypothesis);
        if (ref.isEmpty()) {
            return hyp.isEmpty() ? 0.0 : 1.0;
        }
        int distance = levenshtein(ref, hyp);
        return (double) distance / ref.size();
    }

    private static List<String> tokenise(String text) {
        String normalised = TranscriptNormaliser.normalise(text);
        if (normalised.isEmpty()) {
            return List.of();
        }
        return List.of(normalised.split(" "));
    }

    // Skeletal full-matrix Levenshtein. TODO(hackergarten): two-row space
    // optimisation and S/I/D breakdown.
    private static int levenshtein(List<String> a, List<String> b) {
        int[][] d = new int[a.size() + 1][b.size() + 1];
        for (int i = 0; i <= a.size(); i++) {
            d[i][0] = i;
        }
        for (int j = 0; j <= b.size(); j++) {
            d[0][j] = j;
        }
        for (int i = 1; i <= a.size(); i++) {
            for (int j = 1; j <= b.size(); j++) {
                int cost = a.get(i - 1).equals(b.get(j - 1)) ? 0 : 1;
                d[i][j] = Math.min(
                        Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1),
                        d[i - 1][j - 1] + cost);
            }
        }
        return d[a.size()][b.size()];
    }
}
