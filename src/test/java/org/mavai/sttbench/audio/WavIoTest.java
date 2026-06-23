package org.mavai.sttbench.audio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mavai.outcome.Outcome;

class WavIoTest {

    @Test
    void roundTripsMonoPcmThroughJavaSound(@TempDir Path dir) {
        float[] tone = new float[2000];
        for (int i = 0; i < tone.length; i++) {
            tone[i] = (float) (Math.sin(2 * Math.PI * 440 * i / 16000.0) * 0.5);
        }
        AudioClip clip = new AudioClip(tone, 16000);
        Path file = dir.resolve("tone.wav");

        assertThat(WavIo.write(clip, file).isOk()).isTrue();

        Outcome<AudioClip> read = WavIo.read(file);
        assertThat(read.isOk()).isTrue();
        AudioClip back = read.getOrThrow();
        assertThat(back.sampleRateHz()).isEqualTo(16000);
        assertThat(back.frameCount()).isEqualTo(tone.length);
        for (int i = 0; i < tone.length; i++) {
            // 16-bit quantisation: round-trip is lossy at ~1/32768.
            assertThat(back.samples()[i]).isCloseTo(tone[i], within(1e-3f));
        }
    }

    @Test
    void readingAMissingFileIsAnExpectedFailure(@TempDir Path dir) {
        Outcome<AudioClip> read = WavIo.read(dir.resolve("absent.wav"));
        assertThat(read.isFail()).isTrue();
    }
}
