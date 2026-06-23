package org.mavai.sttbench.eval;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Executable specification for {@link CharacterErrorRate} — a Hackergarten task.
 *
 * <p>{@code CharacterErrorRate.compute} currently throws. <strong>Delete the
 * {@link Disabled} annotation below, run it (red), then make it green.</strong>
 * CER is the character-level twin of WER: the edit distance runs over the
 * characters of the normalised strings, spaces included.
 */
@Disabled("Hackergarten: implement CharacterErrorRate.compute, then delete this @Disabled")
class CharacterErrorRateTest {

    @Test
    @DisplayName("a perfect transcription scores zero, ignoring case and punctuation")
    void perfectMatchIsZero() {
        assertThat(CharacterErrorRate.compute("add two apples", "Add two apples."))
                .isEqualTo(0.0);
    }

    @Test
    @DisplayName("two empty transcripts score zero")
    void bothEmptyIsZero() {
        assertThat(CharacterErrorRate.compute("", "")).isEqualTo(0.0);
    }

    @Test
    @DisplayName("an empty reference against a non-empty hypothesis scores one")
    void emptyReferenceIsOne() {
        assertThat(CharacterErrorRate.compute("", "cat")).isEqualTo(1.0);
    }

    @Test
    @DisplayName("one substituted character in three scores one third")
    void singleCharacterSubstitution() {
        assertThat(CharacterErrorRate.compute("cat", "cot"))
                .isCloseTo(1.0 / 3.0, within(1e-9));
    }

    @Test
    @DisplayName("one inserted character against a three-character reference scores one third")
    void singleCharacterInsertion() {
        assertThat(CharacterErrorRate.compute("cat", "cats"))
                .isCloseTo(1.0 / 3.0, within(1e-9));
    }
}
