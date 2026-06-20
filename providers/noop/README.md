# noop provider

A provider that performs **no real transcription**. It exists so the scaffold
runs end-to-end with no API keys and no network.

By default it returns an empty transcript — which lets you see how the
evaluation metrics and the PUnit contract treat a uselessly-poor transcriber
before you wire a real one. A constructor overload lets it return a fixed
transcript for exercising the metrics with a known input.

Implementation: `org.mavai.sttbench.provider.noop.NoopSttProvider`.

Replace it with a real `SttProvider` to benchmark an actual service.
