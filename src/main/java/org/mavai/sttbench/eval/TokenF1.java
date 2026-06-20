package org.mavai.sttbench.eval;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Token F1: the harmonic mean of token precision and recall between the
 * reference and hypothesis token sets.
 *
 * <p>Token F1 is an order-insensitive, recall-and-precision view of
 * transcription overlap. It is deliberately a <em>token</em> metric — STT
 * systems are expected to transcribe, not paraphrase, so the benchmark
 * scores literal token overlap rather than semantic or cosine similarity.
 * Higher is better; {@code 1.0} is a perfect set match.
 *
 * <p><strong>Skeletal.</strong> The current implementation uses set
 * membership, so repeated tokens are not counted multiply. A multiset
 * (bag-of-words with counts) variant is a sensible Hackergarten extension.
 */
public final class TokenF1 {

    private TokenF1() {
    }

    /**
     * Computes token F1 of {@code hypothesis} against {@code reference}.
     *
     * @param reference  the ground-truth transcript
     * @param hypothesis the provider's transcript
     * @return the F1 score in {@code [0, 1]}; {@code 1.0} when both normalise
     *     to empty, {@code 0.0} when exactly one is empty
     */
    public static double compute(String reference, String hypothesis) {
        Set<String> ref = tokenSet(reference);
        Set<String> hyp = tokenSet(hypothesis);
        if (ref.isEmpty() && hyp.isEmpty()) {
            return 1.0;
        }
        if (ref.isEmpty() || hyp.isEmpty()) {
            return 0.0;
        }
        Set<String> intersection = new HashSet<>(ref);
        intersection.retainAll(hyp);
        int truePositives = intersection.size();
        if (truePositives == 0) {
            return 0.0;
        }
        double precision = (double) truePositives / hyp.size();
        double recall = (double) truePositives / ref.size();
        return 2 * precision * recall / (precision + recall);
    }

    private static Set<String> tokenSet(String text) {
        String normalised = TranscriptNormaliser.normalise(text);
        if (normalised.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(List.of(normalised.split(" ")));
    }
}
