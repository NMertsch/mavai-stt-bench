# Providers

A **provider** adapts a concrete Speech-to-Text service to the benchmark's
uniform shape, the `org.mavai.sttbench.provider.SttProvider` interface:

```java
public interface SttProvider {
    String id();
    Outcome<SttResponse> transcribe(SttRequest request);
}
```

Adding a provider is the primary way to fork and extend this project: drop an
implementation in and benchmark a real service against the bundled corpus and
recipes.

## Expected-failure convention

A transcription the service legitimately could not produce — an unsupported
format, a service error code, a rate-limit rejection — is an *expected
failure*. Return it as data via `Outcome.fail(name, message)`. Reserve thrown
exceptions for genuine defects (bugs, misconfiguration); a thrown exception
aborts the whole run, whereas an `Outcome.fail` is counted as a failed sample.

## Bundled providers

- [`noop/`](noop/) — a no-op provider that returns a canned (by default
  empty) transcript. No API keys, no network. The starting point for a fork.

The Java sources live under
`src/main/java/org/mavai/sttbench/provider/`; this directory holds
provider-specific notes, fixtures, and configuration. Never commit API keys —
see the repository README, *How to add an STT provider*.
