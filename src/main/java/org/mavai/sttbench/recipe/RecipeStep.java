package org.mavai.sttbench.recipe;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One transformation step within a {@link Recipe}.
 *
 * <p>Every step has a {@code type} (e.g. {@code highpass}, {@code lowpass},
 * {@code resample}); the remaining keys are step-specific parameters captured
 * loosely in {@link #parameters()}. Keeping parameters open lets contributors
 * add new step types without changing this class — the audio-generation stage
 * is the place that interprets them.
 *
 * <p><strong>Skeletal.</strong> Parameters are untyped on purpose: no step
 * type is executed yet (see {@code generateAudioVariants}). A Hackergarten
 * session can introduce a typed step hierarchy once the transform engine
 * exists.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class RecipeStep {

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
