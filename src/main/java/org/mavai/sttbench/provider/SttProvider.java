package org.mavai.sttbench.provider;

import org.mavai.outcome.Outcome;

/**
 * A Speech-to-Text service under benchmark.
 *
 * <p>Implementations adapt a concrete STT service (a cloud API, a local
 * model, a mock) to a uniform shape so the benchmark can drive them
 * interchangeably. Adding a provider is the primary fork-and-extend action
 * for this project — drop an implementation under {@code providers/} and
 * register it with the benchmark.
 *
 * <p><strong>Expected-failure convention.</strong> A transcription that the
 * service legitimately could not produce — an unsupported audio format, a
 * service-returned error code, a rate-limit rejection — is an <em>expected
 * failure</em>. It travels as data through {@link Outcome#fail}, not as a
 * thrown exception. Reserve thrown exceptions for genuine defects:
 * programming mistakes, misconfiguration, catastrophe. A thrown exception
 * aborts the benchmark run; an {@code Outcome.fail} is counted as a failed
 * sample. See the mavai {@code Outcome} convention.
 */
public interface SttProvider {

    /**
     * Stable identifier for this provider, used in result and report
     * filenames (e.g. {@code "noop"}, {@code "whisper-local"}).
     *
     * @return the provider id
     */
    String id();

    /**
     * Transcribes a single audio request.
     *
     * @param request the audio to transcribe and its metadata
     * @return {@link Outcome#ok} carrying the transcription on success, or
     *     {@link Outcome#fail} carrying a named, message-bearing failure for
     *     an expected, service-level failure
     */
    Outcome<SttResponse> transcribe(SttRequest request);
}
