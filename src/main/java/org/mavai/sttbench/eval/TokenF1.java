package org.mavai.sttbench.eval;

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
 * <p><strong>Not implemented yet — this is a Hackergarten task.</strong> The
 * behaviour you are implementing towards is pinned by the (currently
 * {@code @Disabled}) spec in {@code TokenF1Test}. Enable that test, then make
 * it green. The baseline spec is <em>set</em> semantics (a token present
 * multiple times counts once); a multiset (bag-of-words with counts) variant
 * is a sensible follow-up. Tokenise through {@link TranscriptNormaliser},
 * count true positives as the set intersection, and combine precision and
 * recall via the harmonic mean.
 */
public final class TokenF1 {

    private TokenF1() {
    }

    /**
     * Computes token F1 of {@code hypothesis} against {@code reference}.
     *
     * <p>Contract the spec test asserts: {@code 1.0} for a perfect (and
     * order-insensitive) match, {@code 1.0} when both sides normalise to empty,
     * {@code 0.0} when exactly one side is empty, and {@code 0.0} when there is
     * no token overlap.
     *
     * @param reference  the ground-truth transcript
     * @param hypothesis the provider's transcript
     * @return the F1 score in {@code [0, 1]}
     */
    public static double compute(String reference, String hypothesis) {
        throw new UnsupportedOperationException(
                "Hackergarten: implement Token F1 — see TokenF1Test for the spec");
    }
}
