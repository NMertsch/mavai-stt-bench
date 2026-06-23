package org.mavai.sttbench.audio;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mavai.sttbench.recipe.AudioRecipe;
import org.mavai.sttbench.recipe.AudioRecipeStep;

class AudioVariantGeneratorTest {

    private static AudioRecipeStep step(String type, String key, Number value) {
        AudioRecipeStep s = new AudioRecipeStep();
        s.setType(type);
        s.parameters().put(key, value);
        return s;
    }

    private static void writeTone(Path file, int rate, int frames) {
        float[] s = new float[frames];
        for (int i = 0; i < frames; i++) {
            s[i] = (float) (Math.sin(2 * Math.PI * 440 * i / rate) * 0.5);
        }
        WavIo.write(new AudioClip(s, rate), file).getOrThrow();
    }

    @Test
    void generatesImplementedRecipesAndReportsUnimplementedOnes(@TempDir Path tmp) {
        Path corpus = tmp.resolve("corpus");
        Path out = tmp.resolve("out");
        writeTone(corpus.resolve("clip1.wav"), 16000, 8000);

        AudioRecipe clean = new AudioRecipe("clean", "control", List.of());
        AudioRecipe telephone = new AudioRecipe("telephone-bandwidth", "phone", List.of(
                step("highpass", "frequencyHz", 300),
                step("lowpass", "frequencyHz", 3400),
                step("resample", "sampleRateHz", 8000)));
        AudioRecipe cafe = new AudioRecipe("cafe-heavy", "noise", List.of(
                step("mixNoise", "snrDb", 5)));

        AudioVariantGenerator.Summary summary =
                new AudioVariantGenerator().generate(corpus, List.of(clean, telephone, cafe), out);

        assertThat(summary.written()).isEqualTo(2);
        assertThat(summary.failed()).isEqualTo(1);
        assertThat(summary.failures()).anyMatch(f -> f.contains("cafe-heavy"));

        Path cleanVariant = out.resolve("clean").resolve("clip1__clean.wav");
        Path phoneVariant = out.resolve("telephone-bandwidth").resolve("clip1__telephone-bandwidth.wav");
        assertThat(cleanVariant).exists();
        assertThat(phoneVariant).exists();
        // Provenance sidecar written beside the variant — the audit hook.
        assertThat(cleanVariant.resolveSibling("clip1__clean.wav.provenance.json")).exists();
        // telephone-bandwidth resampled to 8 kHz.
        assertThat(WavIo.read(phoneVariant).getOrThrow().sampleRateHz()).isEqualTo(8000);
        // The unimplemented café recipe produced no file.
        assertThat(Files.exists(out.resolve("cafe-heavy"))).isFalse();
    }

    @Test
    void emptyCorpusProducesNothingButDoesNotThrow(@TempDir Path tmp) {
        AudioVariantGenerator.Summary summary = new AudioVariantGenerator()
                .generate(tmp.resolve("missing"), List.of(new AudioRecipe("clean", "", List.of())),
                        tmp.resolve("out"));
        assertThat(summary.written()).isZero();
        assertThat(summary.failed()).isZero();
    }
}
