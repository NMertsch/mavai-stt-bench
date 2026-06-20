package org.mavai.sttbench.provider;

import java.util.Objects;

/**
 * The result of a successful transcription.
 *
 * @param providerId    id of the provider that produced this transcript
 * @param transcript    the raw, un-normalised transcript text as returned by
 *                      the service
 */
public record SttResponse(String providerId, String transcript) {

    public SttResponse {
        Objects.requireNonNull(providerId, "providerId");
        Objects.requireNonNull(transcript, "transcript");
    }
}
