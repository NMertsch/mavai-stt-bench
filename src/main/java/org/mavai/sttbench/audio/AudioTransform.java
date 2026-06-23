package org.mavai.sttbench.audio;

import org.mavai.outcome.Outcome;

/**
 * A single audio transformation: one recipe step realised as a function from
 * one {@link AudioClip} to another.
 *
 * <p>Returns an {@link Outcome} rather than throwing: an invalid parameter (a
 * cutoff above Nyquist, a non-positive sample rate) is an <em>expected
 * failure</em> that the generator reports and skips, not a defect that aborts
 * the run. A transform must not mutate its input — it returns a new clip.
 */
@FunctionalInterface
public interface AudioTransform {

    /**
     * Applies this transform to {@code clip}.
     *
     * @param clip the input clip
     * @return {@code ok} with the transformed clip, or {@code fail} on an
     *         expected, parameter-level failure
     */
    Outcome<AudioClip> apply(AudioClip clip);
}
