package org.mavai.sttbench.audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.mavai.outcome.Outcome;

/**
 * Reads and writes mono 16-bit PCM WAV files using the JDK's built-in
 * {@code javax.sound.sampled} — no third-party audio dependency.
 *
 * <p>On {@link #read} the source is converted to signed 16-bit PCM (Java Sound
 * supplies the bit-depth conversion) and any multi-channel input is downmixed
 * to mono. On {@link #write} the clip is encoded back to a 16-bit PCM WAV.
 *
 * <p>An unreadable or unsupported file (e.g. a format Java Sound has no decoder
 * for, such as MP3 without an SPI plugin) is an <em>expected failure</em>: both
 * methods return an {@link Outcome} rather than throwing, so the generator can
 * report it and carry on.
 */
public final class WavIo {

    private WavIo() {
    }

    /**
     * Decodes a WAV file to a mono {@link AudioClip}.
     *
     * @param file the source {@code .wav} file
     * @return {@code ok} with the decoded clip, or {@code fail}
     *         ({@code "audio-read-error"}) if it cannot be read or decoded
     */
    public static Outcome<AudioClip> read(Path file) {
        Objects.requireNonNull(file, "file");
        try (AudioInputStream in = AudioSystem.getAudioInputStream(file.toFile())) {
            AudioFormat base = in.getFormat();
            int channels = Math.max(1, base.getChannels());
            float rate = base.getSampleRate();
            AudioFormat pcm = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED, rate, 16, channels, channels * 2, rate, false);
            try (AudioInputStream pcmIn = AudioSystem.getAudioInputStream(pcm, in)) {
                byte[] bytes = pcmIn.readAllBytes();
                int frames = bytes.length / (2 * channels);
                float[] mono = new float[frames];
                for (int f = 0; f < frames; f++) {
                    int acc = 0;
                    for (int c = 0; c < channels; c++) {
                        int idx = (f * channels + c) * 2;
                        short sample = (short) ((bytes[idx] & 0xFF) | (bytes[idx + 1] << 8));
                        acc += sample;
                    }
                    mono[f] = (acc / (float) channels) / 32768.0f;
                }
                return Outcome.ok(new AudioClip(mono, Math.round(rate)));
            }
        } catch (UnsupportedAudioFileException | IOException | IllegalArgumentException e) {
            return Outcome.fail("audio-read-error",
                    "Could not read %s: %s".formatted(file, e.getMessage()));
        }
    }

    /**
     * Encodes a mono clip to a 16-bit PCM WAV file, creating parent directories.
     *
     * @param clip the clip to write
     * @param file the destination {@code .wav} file
     * @return {@code ok}, or {@code fail} ({@code "audio-write-error"})
     */
    public static Outcome<Void> write(AudioClip clip, Path file) {
        Objects.requireNonNull(clip, "clip");
        Objects.requireNonNull(file, "file");
        float[] samples = clip.samples();
        byte[] bytes = new byte[samples.length * 2];
        for (int i = 0; i < samples.length; i++) {
            int v = Math.round(Math.max(-1.0f, Math.min(1.0f, samples[i])) * 32767.0f);
            bytes[i * 2] = (byte) (v & 0xFF);
            bytes[i * 2 + 1] = (byte) ((v >> 8) & 0xFF);
        }
        AudioFormat out = new AudioFormat(clip.sampleRateHz(), 16, 1, true, false);
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (AudioInputStream stream =
                    new AudioInputStream(new ByteArrayInputStream(bytes), out, samples.length)) {
                AudioSystem.write(stream, AudioFileFormat.Type.WAVE, file.toFile());
            }
            return Outcome.ok();
        } catch (IOException | IllegalArgumentException e) {
            return Outcome.fail("audio-write-error",
                    "Could not write %s: %s".formatted(file, e.getMessage()));
        }
    }
}
