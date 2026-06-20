package org.mavai.sttbench.provider.noop;

import org.mavai.outcome.Outcome;
import org.mavai.sttbench.provider.SttProvider;
import org.mavai.sttbench.provider.SttRequest;
import org.mavai.sttbench.provider.SttResponse;

/**
 * A provider that performs no real transcription.
 *
 * <p>It exists so the benchmark scaffold runs end-to-end with no API keys
 * and no network — the obvious starting point for a fork. By default it
 * returns an empty transcript, which lets contributors see how the
 * evaluation metrics and the PUnit contract treat a uselessly-poor
 * transcriber before they wire a real one.
 *
 * <p>Replace this with a real {@link SttProvider} to benchmark an actual
 * service. This implementation never throws and never reaches the network.
 */
public final class NoopSttProvider implements SttProvider {

    private final String transcript;

    /** Creates a noop provider that returns an empty transcript. */
    public NoopSttProvider() {
        this("");
    }

    /**
     * Creates a noop provider that returns a fixed transcript regardless of
     * the audio — useful for exercising the metrics with a known input.
     *
     * @param transcript the canned transcript to return for every request
     */
    public NoopSttProvider(String transcript) {
        this.transcript = transcript;
    }

    @Override
    public String id() {
        return "noop";
    }

    @Override
    public Outcome<SttResponse> transcribe(SttRequest request) {
        // No I/O, no network, no failure path to model yet. A real provider
        // would return Outcome.fail(...) for an expected service-level error.
        return Outcome.ok(new SttResponse(id(), transcript));
    }
}
