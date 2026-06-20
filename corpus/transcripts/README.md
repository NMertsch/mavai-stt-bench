# Corpus transcripts

Reference transcripts, one per clip, named `<clip-id>.txt` (e.g.
`shopping_001.txt`). Each holds the exact text that was read aloud to record
the matching `../audio/<clip-id>.wav`.

The transcript is the **ground truth**. The benchmark normalises both it and
the provider's output before scoring (see
`org.mavai.sttbench.eval.TranscriptNormaliser`), so casing and punctuation
need not match a particular convention — but the words should be exactly what
was spoken.

Placeholder directory — add your own transcripts.
