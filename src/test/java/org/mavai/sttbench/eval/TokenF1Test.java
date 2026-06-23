package org.mavai.sttbench.eval;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Executable specification for {@link TokenF1} — a Hackergarten task.
 *
 * <p>{@code TokenF1.compute} currently throws. <strong>Delete the
 * {@link Disabled} annotation below, run it (red), then make it green.</strong>
 * The spec is <em>set</em> semantics: a token present multiple times counts
 * once. The partial-overlap cases pin precision and recall independently, so a
 * passing run is a correct harmonic mean — not a shortcut that only works when
 * precision equals recall.
 */
@Disabled("Hackergarten: implement TokenF1.compute, then delete this @Disabled")
class TokenF1Test {

    @Test
    @DisplayName("a perfect transcription scores one, ignoring case and punctuation")
    void perfectMatchIsOne() {
        assertThat(TokenF1.compute("add two apples", "Add two apples."))
                .isCloseTo(1.0, within(1e-9));
    }

    @Test
    @DisplayName("token overlap is order-insensitive")
    void orderInsensitive() {
        assertThat(TokenF1.compute("add two apples", "apples add two"))
                .isCloseTo(1.0, within(1e-9));
    }

    @Test
    @DisplayName("two empty transcripts score one")
    void bothEmptyIsOne() {
        assertThat(TokenF1.compute("", "")).isCloseTo(1.0, within(1e-9));
    }

    @Test
    @DisplayName("exactly one empty transcript scores zero")
    void oneEmptyIsZero() {
        assertThat(TokenF1.compute("add two apples", "")).isEqualTo(0.0);
        assertThat(TokenF1.compute("", "add two apples")).isEqualTo(0.0);
    }

    @Test
    @DisplayName("no shared tokens scores zero")
    void noOverlapIsZero() {
        assertThat(TokenF1.compute("add two apples", "remove four oranges"))
                .isEqualTo(0.0);
    }

    @Test
    @DisplayName("two shared tokens out of three each scores two thirds")
    void balancedPartialOverlap() {
        assertThat(TokenF1.compute("add two apples", "add three apples"))
                .isCloseTo(2.0 / 3.0, within(1e-9));
    }

    @Test
    @DisplayName("high precision and lower recall combine via the harmonic mean")
    void precisionDiffersFromRecall() {
        // hyp tokens all correct (precision 1.0) but cover half the reference
        // (recall 0.5): F1 = 2 * 1.0 * 0.5 / (1.0 + 0.5) = 2/3.
        assertThat(TokenF1.compute("one two three four", "one two"))
                .isCloseTo(2.0 / 3.0, within(1e-9));
    }
}
