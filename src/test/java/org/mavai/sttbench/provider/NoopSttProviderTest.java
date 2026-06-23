package org.mavai.sttbench.provider;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.mavai.outcome.Outcome;
import org.mavai.sttbench.provider.noop.NoopSttProvider;

class NoopSttProviderTest {

    @Test
    void returnsOkWithCannedTranscript() {
        SttProvider provider = new NoopSttProvider("add two apples");
        SttRequest request = new SttRequest(
                "shopping_001", "clean", Path.of("build/generated-audio/clean/shopping_001.wav"), "en-GB");

        Outcome<SttResponse> outcome = provider.transcribe(request);

        assertThat(outcome.isOk()).isTrue();
        assertThat(outcome.getOrThrow().transcript()).isEqualTo("add two apples");
        assertThat(outcome.getOrThrow().providerId()).isEqualTo("noop");
    }
}
