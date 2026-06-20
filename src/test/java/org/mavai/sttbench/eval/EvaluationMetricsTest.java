package org.mavai.sttbench.eval;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Skeletal checks for the evaluation metrics.
 *
 * <p>These also serve as the first proof that the test stack — PUnit on top
 * of JUnit 6, with AssertJ — compiles and runs in this project. Validating
 * PUnit compatibility with JUnit 6 is a stated project goal; a green run here
 * is the smallest evidence of it.
 */
class EvaluationMetricsTest {

    @Test
    @DisplayName("normalisation lower-cases, strips punctuation, collapses whitespace")
    void normalisesText() {
        assertThat(TranscriptNormaliser.normalise("  Hello,  WORLD! "))
                .isEqualTo("hello world");
    }

    @Test
    @DisplayName("a perfect transcription scores zero error and full overlap")
    void perfectMatchScores() {
        String reference = "add two apples";
        String hypothesis = "Add two apples.";
        assertThat(WordErrorRate.compute(reference, hypothesis)).isEqualTo(0.0);
        assertThat(CharacterErrorRate.compute(reference, hypothesis)).isEqualTo(0.0);
        assertThat(TokenF1.compute(reference, hypothesis)).isCloseTo(1.0, within(1e-9));
    }

    @Test
    @DisplayName("a single wrong word raises WER and lowers Token F1")
    void singleSubstitution() {
        String reference = "add two apples";
        String hypothesis = "add three apples";
        assertThat(WordErrorRate.compute(reference, hypothesis)).isCloseTo(1.0 / 3.0, within(1e-9));
        assertThat(TokenF1.compute(reference, hypothesis)).isLessThan(1.0);
    }
}
