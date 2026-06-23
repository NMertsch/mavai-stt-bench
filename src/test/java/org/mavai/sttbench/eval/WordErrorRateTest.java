package org.mavai.sttbench.eval;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Executable specification for {@link WordErrorRate} — a Hackergarten task.
 *
 * <p>{@code WordErrorRate.compute} currently throws. This class is the target:
 * <strong>delete the {@link Disabled} annotation below, run it (red), then make
 * it green.</strong> The cases pin the whole contract — the perfect match, the
 * empty-input edges, and one each of substitution / insertion / deletion —
 * so a passing run is a complete, correct WER.
 */
@Disabled("Hackergarten: implement WordErrorRate.compute, then delete this @Disabled")
class WordErrorRateTest {

    @Test
    @DisplayName("a perfect transcription scores zero, ignoring case and punctuation")
    void perfectMatchIsZero() {
        assertThat(WordErrorRate.compute("add two apples", "Add two apples."))
                .isEqualTo(0.0);
    }

    @Test
    @DisplayName("two empty transcripts score zero")
    void bothEmptyIsZero() {
        assertThat(WordErrorRate.compute("", "")).isEqualTo(0.0);
    }

    @Test
    @DisplayName("an empty reference against a non-empty hypothesis scores one")
    void emptyReferenceIsOne() {
        assertThat(WordErrorRate.compute("", "add two apples")).isEqualTo(1.0);
    }

    @Test
    @DisplayName("one substituted word in three scores one third")
    void singleSubstitution() {
        assertThat(WordErrorRate.compute("add two apples", "add three apples"))
                .isCloseTo(1.0 / 3.0, within(1e-9));
    }

    @Test
    @DisplayName("one deleted word in three scores one third")
    void singleDeletion() {
        assertThat(WordErrorRate.compute("add two apples", "add apples"))
                .isCloseTo(1.0 / 3.0, within(1e-9));
    }

    @Test
    @DisplayName("one inserted word against a three-word reference scores one third")
    void singleInsertion() {
        assertThat(WordErrorRate.compute("add two apples", "add two more apples"))
                .isCloseTo(1.0 / 3.0, within(1e-9));
    }

    @Test
    @DisplayName("WER can exceed one when there are more errors than reference words")
    void canExceedOne() {
        assertThat(WordErrorRate.compute("go", "please go to the shop"))
                .isGreaterThan(1.0);
    }
}
