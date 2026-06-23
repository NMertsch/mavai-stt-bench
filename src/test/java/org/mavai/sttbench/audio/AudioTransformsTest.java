package org.mavai.sttbench.audio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Test;
import org.mavai.outcome.Outcome;
import org.mavai.sttbench.recipe.AudioRecipeStep;

class AudioTransformsTest {

    private static AudioRecipeStep step(String type) {
        AudioRecipeStep s = new AudioRecipeStep();
        s.setType(type);
        return s;
    }

    private static AudioClip tone(double freqHz, int rate, int frames) {
        float[] s = new float[frames];
        for (int i = 0; i < frames; i++) {
            s[i] = (float) (Math.sin(2 * Math.PI * freqHz * i / rate) * 0.5);
        }
        return new AudioClip(s, rate);
    }

    private static double rms(float[] s) {
        double sum = 0;
        for (float v : s) {
            sum += (double) v * v;
        }
        return Math.sqrt(sum / s.length);
    }

    @Test
    void gainScalesAmplitudeByDecibels() {
        AudioRecipeStep gain = step("gain");
        gain.parameters().put("db", -20.0); // factor 0.1
        AudioClip in = tone(440, 16000, 1000);

        Outcome<AudioTransform> t = AudioTransforms.forStep(gain);
        assertThat(t.isOk()).isTrue();
        AudioClip out = t.getOrThrow().apply(in).getOrThrow();

        assertThat(rms(out.samples())).isCloseTo(rms(in.samples()) * 0.1, within(1e-4));
    }

    @Test
    void lowpassAttenuatesHighFrequenciesAndPassesLow() {
        AudioRecipeStep lowpass = step("lowpass");
        lowpass.parameters().put("frequencyHz", 1000);
        AudioTransform t = AudioTransforms.forStep(lowpass).getOrThrow();

        AudioClip high = tone(7000, 16000, 4000);
        AudioClip low = tone(200, 16000, 4000);
        double highRatio = rms(t.apply(high).getOrThrow().samples()) / rms(high.samples());
        double lowRatio = rms(t.apply(low).getOrThrow().samples()) / rms(low.samples());

        assertThat(highRatio).isLessThan(0.2);   // strongly attenuated
        assertThat(lowRatio).isGreaterThan(0.8);  // largely preserved
    }

    @Test
    void resampleChangesRateAndProportionalLength() {
        AudioRecipeStep resample = step("resample");
        resample.parameters().put("sampleRateHz", 8000);
        AudioClip in = tone(440, 16000, 1000);

        AudioClip out = AudioTransforms.forStep(resample).getOrThrow().apply(in).getOrThrow();

        assertThat(out.sampleRateHz()).isEqualTo(8000);
        assertThat(out.frameCount()).isEqualTo(500);
    }

    @Test
    void missingParameterIsAnExpectedFailure() {
        Outcome<AudioTransform> t = AudioTransforms.forStep(step("lowpass")); // no frequencyHz
        assertThat(t.isFail()).isTrue();
    }

    @Test
    void unimplementedAndUnknownStepTypesAreExpectedFailures() {
        assertThat(AudioTransforms.forStep(step("reverb")).isFail()).isTrue();
        assertThat(AudioTransforms.forStep(step("frobnicate")).isFail()).isTrue();
    }
}
