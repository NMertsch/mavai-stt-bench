package org.mavai.sttbench.audio;

import java.util.Objects;

/**
 * A decoded, in-memory <strong>mono</strong> PCM clip: normalised float samples
 * in {@code [-1.0, 1.0]} paired with their sample rate.
 *
 * <p>Mono by design — STT ingest is overwhelmingly mono, so {@link WavIo}
 * downmixes multi-channel sources on read. Every {@link AudioTransform} operates
 * on this single sample array, which keeps the transform chain uniform.
 *
 * <p>The {@code samples} array is owned by the clip and not defensively copied;
 * transforms return a <em>new</em> {@code AudioClip} rather than mutating their
 * input, so sharing is safe in the one-shot generation pipeline.
 *
 * @param samples      normalised PCM samples in {@code [-1.0, 1.0]}
 * @param sampleRateHz the sample rate in hertz; positive
 */
public record AudioClip(float[] samples, int sampleRateHz) {

    public AudioClip {
        Objects.requireNonNull(samples, "samples");
        if (sampleRateHz <= 0) {
            throw new IllegalArgumentException("sampleRateHz must be positive: " + sampleRateHz);
        }
    }

    /** @return the number of samples (frames) in this mono clip. */
    public int frameCount() {
        return samples.length;
    }

    /** @return the clip duration in seconds. */
    public double durationSeconds() {
        return (double) samples.length / sampleRateHz;
    }
}
