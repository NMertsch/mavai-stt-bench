# Contracts

This project ships a single, predefined PUnit **service contract** for STT
benchmarking: `org.mavai.sttbench.contract.SttServiceContract`.

It is deliberately not generic. The contract encodes what it means for an STT
system to transcribe well, as PUnit pass-rate criteria so the engine can derive
statistical verdicts across many clips and recipes:

- **Non-empty transcript** — the provider returned content.
- **Normalised exact match** — the normalised transcript equals the reference.
- **WER below threshold** — Word Error Rate is within tolerance.
- **CER below threshold** — Character Error Rate is within tolerance.
- **Token F1 above threshold** — literal token overlap is high enough.

Thresholds live on the contract's factor record
(`SttServiceContract.SttTuning`); varying them is how an experiment sweeps
acceptance strictness.

By design the contract uses **literal** transcription metrics — never semantic
or cosine similarity, and **no manually-annotated keyword-retention criteria**.
STT systems are expected to transcribe, not paraphrase.

This directory holds contract-related configuration and notes; the contract
source lives under `src/main/java/org/mavai/sttbench/contract/`.
