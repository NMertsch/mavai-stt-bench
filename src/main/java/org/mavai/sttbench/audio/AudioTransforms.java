package org.mavai.sttbench.audio;

import java.util.Objects;
import org.mavai.outcome.Outcome;
import org.mavai.sttbench.recipe.AudioRecipeStep;

/**
 * Resolves a {@link AudioRecipeStep} to the {@link AudioTransform} that realises
 * it, and houses the implemented transforms.
 *
 * <p><strong>The central extension point.</strong> {@link #forStep} is a switch
 * on the step's {@code type}. Adding a degradation is: implement a transform
 * factory here and add a {@code case}. An unknown or not-yet-implemented type
 * returns {@link Outcome#fail} — the generator collects and reports it rather
 * than crashing, so a half-finished recipe set still runs end-to-end.
 *
 * <p>Implemented: {@code gain}, {@code highpass}, {@code lowpass},
 * {@code resample} — enough for the {@code clean} and {@code telephone-bandwidth}
 * recipes to run fully. The {@code cafe-*} recipes need {@code mixNoise} and
 * {@code reverb}, which are deliberately left as Hackergarten extension points
 * (they want noise corpora and a convolution/Schroeder reverb respectively).
 */
public final class AudioTransforms {

    private AudioTransforms() {
    }

    /**
     * Resolves a recipe step to its transform.
     *
     * @param step the recipe step
     * @return {@code ok} with the transform, or {@code fail} for a missing
     *         parameter, an unimplemented step type, or an unknown step type
     */
    public static Outcome<AudioTransform> forStep(AudioRecipeStep step) {
        Objects.requireNonNull(step, "step");
        String type = step.type();
        if (type == null) {
            return Outcome.fail("missing-step-type", "Recipe step has no 'type'");
        }
        return switch (type) {
            case "gain" -> param(step, "db").map(db -> gain(db));
            case "highpass" -> param(step, "frequencyHz").map(f -> biquad(BiquadKind.HIGHPASS, f));
            case "lowpass" -> param(step, "frequencyHz").map(f -> biquad(BiquadKind.LOWPASS, f));
            case "resample" -> param(step, "sampleRateHz").map(r -> resample((int) Math.round(r)));

            // ── Hackergarten extension points: implement, then add a case. ──
            case "mixNoise", "reverb" -> Outcome.fail("unimplemented-step-type",
                    ("Step type '%s' is a Hackergarten extension point — implement a transform "
                            + "for it in AudioTransforms and add a case to forStep").formatted(type));

            default -> Outcome.fail("unknown-step-type", "Unknown recipe step type '%s'".formatted(type));
        };
    }

    /** Reads a required numeric step parameter, as an expected failure if absent. */
    private static Outcome<Double> param(AudioRecipeStep step, String key) {
        Object value = step.parameters().get(key);
        if (value instanceof Number n) {
            return Outcome.ok(n.doubleValue());
        }
        return Outcome.fail("missing-step-parameter",
                "Step '%s' requires numeric parameter '%s'".formatted(step.type(), key));
    }

    // ── Implemented transforms ───────────────────────────────────────────────

    /** Linear gain by {@code db} decibels, with hard clipping at full scale. */
    static AudioTransform gain(double db) {
        double factor = Math.pow(10.0, db / 20.0);
        return clip -> {
            float[] in = clip.samples();
            float[] out = new float[in.length];
            for (int i = 0; i < in.length; i++) {
                out[i] = (float) (in[i] * factor);
            }
            return Outcome.ok(new AudioClip(out, clip.sampleRateHz()));
        };
    }

    private enum BiquadKind {
        LOWPASS,
        HIGHPASS
    }

    /**
     * A second-order Butterworth (Q = 1/√2) biquad low- or high-pass, using the
     * standard RBJ-cookbook coefficients in Direct Form I.
     */
    private static AudioTransform biquad(BiquadKind kind, double cutoffHz) {
        return clip -> {
            double fs = clip.sampleRateHz();
            double nyquist = fs / 2.0;
            if (cutoffHz <= 0 || cutoffHz >= nyquist) {
                return Outcome.fail("invalid-cutoff",
                        "Cutoff %.1f Hz must lie in (0, Nyquist=%.1f)".formatted(cutoffHz, nyquist));
            }
            double w0 = 2 * Math.PI * cutoffHz / fs;
            double cos = Math.cos(w0);
            double alpha = Math.sin(w0) / (2 * Math.sqrt(0.5));
            double a0 = 1 + alpha;
            double a1 = -2 * cos;
            double a2 = 1 - alpha;
            double b0;
            double b1;
            double b2;
            if (kind == BiquadKind.LOWPASS) {
                b0 = (1 - cos) / 2;
                b1 = 1 - cos;
                b2 = (1 - cos) / 2;
            } else {
                b0 = (1 + cos) / 2;
                b1 = -(1 + cos);
                b2 = (1 + cos) / 2;
            }
            double nb0 = b0 / a0;
            double nb1 = b1 / a0;
            double nb2 = b2 / a0;
            double na1 = a1 / a0;
            double na2 = a2 / a0;

            float[] in = clip.samples();
            float[] out = new float[in.length];
            double x1 = 0;
            double x2 = 0;
            double y1 = 0;
            double y2 = 0;
            for (int i = 0; i < in.length; i++) {
                double x0 = in[i];
                double y0 = nb0 * x0 + nb1 * x1 + nb2 * x2 - na1 * y1 - na2 * y2;
                out[i] = (float) y0;
                x2 = x1;
                x1 = x0;
                y2 = y1;
                y1 = y0;
            }
            return Outcome.ok(new AudioClip(out, clip.sampleRateHz()));
        };
    }

    /**
     * Resamples to {@code targetRateHz} by linear interpolation. Recipes that
     * downsample should low-pass first (as {@code telephone-bandwidth} does), so
     * the signal is band-limited below the new Nyquist and aliasing is
     * negligible; a polyphase/anti-aliased resampler is a sensible upgrade.
     */
    static AudioTransform resample(int targetRateHz) {
        return clip -> {
            if (targetRateHz <= 0) {
                return Outcome.fail("invalid-sample-rate",
                        "Target sample rate must be positive: " + targetRateHz);
            }
            int srcRate = clip.sampleRateHz();
            if (targetRateHz == srcRate) {
                return Outcome.ok(clip);
            }
            float[] in = clip.samples();
            if (in.length == 0) {
                return Outcome.ok(new AudioClip(new float[0], targetRateHz));
            }
            int outLen = (int) Math.max(1, Math.round((long) in.length * targetRateHz / (double) srcRate));
            float[] out = new float[outLen];
            double ratio = (double) srcRate / targetRateHz;
            for (int i = 0; i < outLen; i++) {
                double srcPos = i * ratio;
                int j = (int) Math.floor(srcPos);
                double frac = srcPos - j;
                float a = in[Math.min(j, in.length - 1)];
                float b = in[Math.min(j + 1, in.length - 1)];
                out[i] = (float) (a + (b - a) * frac);
            }
            return Outcome.ok(new AudioClip(out, targetRateHz));
        };
    }
}
