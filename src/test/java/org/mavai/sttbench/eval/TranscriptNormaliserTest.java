package org.mavai.sttbench.eval;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Checks for the transcript normaliser — the one fully-implemented evaluator
 * helper, kept as the worked reference for the metric tasks alongside it.
 *
 * <p>These also serve as the first proof that the test stack — PUnit on top of
 * JUnit 6, with AssertJ — compiles and runs in this project. Validating PUnit
 * compatibility with JUnit 6 is a stated project goal; a green run here is the
 * smallest evidence of it. Unlike the metric specs in this package, this test
 * is enabled: it passes today.
 */
class TranscriptNormaliserTest {

    @Test
    @DisplayName("normalisation lower-cases, strips punctuation, collapses whitespace")
    void normalisesText() {
        assertThat(TranscriptNormaliser.normalise("  Hello,  WORLD! "))
                .isEqualTo("hello world");
    }

    @Test
    @DisplayName("normalisation maps null to the empty string")
    void normalisesNull() {
        assertThat(TranscriptNormaliser.normalise(null)).isEqualTo("");
    }

    @Test
    @DisplayName("an already-canonical transcript is unchanged")
    void leavesCanonicalTextAlone() {
        assertThat(TranscriptNormaliser.normalise("add two apples"))
                .isEqualTo("add two apples");
    }
}
