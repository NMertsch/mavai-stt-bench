package org.mavai.sttbench.recipe;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One transformation step within an {@link AudioRecipe}.
 *
 * <p>Every step has a {@code type} (e.g. {@code highpass}, {@code lowpass},
 * {@code resample}); the remaining keys are step-specific parameters captured
 * loosely in {@link #parameters()}. Keeping parameters open lets contributors
 * add new step types without changing this class — the audio-generation stage
 * is the place that interprets them.
 *
 * <p>The transform engine ({@code org.mavai.sttbench.audio.AudioTransforms})
 * interprets these: it implements {@code gain}, {@code highpass},
 * {@code lowpass} and {@code resample}, and treats {@code mixNoise}/{@code reverb}
 * (and any unknown type) as expected failures to be filled in. Introducing a
 * typed step hierarchy is a sensible Hackergarten follow-up.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class AudioRecipeStep {

    private String type;
    private final Map<String, Object> parameters = new LinkedHashMap<>();

    public String type() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Step-specific parameters keyed by their YAML field name (e.g.
     * {@code frequencyHz}, {@code sampleRateHz}).
     *
     * @return a live view of the captured parameters
     */
    @JsonAnyGetter
    public Map<String, Object> parameters() {
        return parameters;
    }

    @JsonAnySetter
    void putParameter(String key, Object value) {
        parameters.put(key, value);
    }
}
