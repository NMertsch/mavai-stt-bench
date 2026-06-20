package org.mavai.sttbench.contract;

import static org.mavai.punit.api.criterion.Criteria.empirical;
import static org.mavai.punit.api.criterion.Criteria.of;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.mavai.outcome.Outcome;
import org.mavai.punit.api.ServiceContract;
import org.mavai.punit.api.TokenTracker;
import org.mavai.punit.api.covariate.Covariate;
import org.mavai.punit.api.covariate.CovariateCategory;
import org.mavai.punit.api.criterion.Criteria;
import org.mavai.sttbench.eval.CharacterErrorRate;
import org.mavai.sttbench.eval.TokenF1;
import org.mavai.sttbench.eval.TranscriptNormaliser;
import org.mavai.sttbench.eval.WordErrorRate;
import org.mavai.sttbench.provider.SttProvider;
import org.mavai.sttbench.provider.SttRequest;
import org.mavai.sttbench.provider.SttResponse;

/**
 * The predefined PUnit service contract for Speech-to-Text benchmarking.
 *
 * <p>This is the single, opinionated contract every provider is judged
 * against. It is deliberately not generic: the criteria below encode what it
 * means for an STT system to transcribe well, expressed as PUnit pass-rate
 * criteria so the engine can derive statistical verdicts across many clips
 * and recipes.
 *
 * <p><strong>Factor record.</strong> {@link SttTuning} carries the thresholds
 * the contract judges against. Varying the tuning (e.g. tightening the WER
 * threshold) is how an experiment sweeps acceptance strictness.
 *
 * <p><strong>Input / output.</strong> The input is an {@link SttRequest}
 * (one audio clip, possibly recipe-transformed). The output value the engine
 * judges is the raw transcript {@code String} the provider returned.
 *
 * <p><strong>Reference transcripts</strong> are the ground truth: the text
 * originally read to record the corpus clip. They are resolved per clip and
 * compared against the provider's transcript under
 * {@link TranscriptNormaliser normalisation}. The contract scores literal
 * token / word / character overlap — never semantic or cosine similarity —
 * because STT systems are expected to transcribe, not paraphrase.
 *
 * <p>The provider is a {@link CovariateCategory#CONFIGURATION} covariate, so
 * a baseline measured under one provider can never silently match a test run
 * under another.
 *
 * <p><strong>Skeletal.</strong> Reference resolution is provided by a
 * {@code referenceResolver} closure; the corpus-loading wiring that supplies
 * it end-to-end is a Hackergarten extension point (see
 * {@code runSttBenchmark}). The criteria set is complete enough to be
 * meaningful and intentionally leaves keyword-retention criteria out by
 * design.
 */
public final class SttServiceContract
        implements ServiceContract<SttServiceContract.SttTuning, SttRequest, String> {

    private final SttProvider provider;
    private final SttTuning tuning;
    private final ReferenceResolver references;

    /**
     * Resolves the ground-truth reference transcript for a request's clip.
     * A real implementation reads {@code corpus/transcripts/<clipId>.txt}.
     */
    @FunctionalInterface
    public interface ReferenceResolver {
        /**
         * @param request the request whose clip's reference is wanted
         * @return the reference transcript text, or {@code null} if none exists
         */
        String referenceFor(SttRequest request);
    }

    public SttServiceContract(SttProvider provider, SttTuning tuning, ReferenceResolver references) {
        this.provider = provider;
        this.tuning = tuning;
        this.references = references;
    }

    @Override
    public Criteria<String> criteria() {
        return of(
                empirical().<String>passRate()
                        .name("non-empty-transcript")
                        .satisfies("Transcript is non-empty",
                                this::checkNonEmpty),
                empirical().<String>passRate()
                        .name("normalised-exact-match")
                        .satisfies("Normalised transcript exactly matches reference",
                                this::checkExactMatch),
                empirical().<String>passRate()
                        .name("wer-below-threshold")
                        .satisfies("Word Error Rate below threshold",
                                this::checkWer),
                empirical().<String>passRate()
                        .name("cer-below-threshold")
                        .satisfies("Character Error Rate below threshold",
                                this::checkCer),
                empirical().<String>passRate()
                        .name("token-f1-above-threshold")
                        .satisfies("Token F1 above threshold",
                                this::checkTokenF1));
    }

    private Outcome<Void> checkNonEmpty(String transcript) {
        return TranscriptNormaliser.normalise(transcript).isEmpty()
                ? Outcome.fail("empty-transcript", "Provider returned an empty transcript")
                : Outcome.ok();
    }

    private Outcome<Void> checkExactMatch(String transcript) {
        // NOTE: the reference is resolved from the most recent invoke via the
        // resolver bound at construction. Wiring the per-sample input through
        // to the criterion is a Hackergarten extension; until then this scores
        // against the resolver's clip-independent reference.
        String reference = TranscriptNormaliser.normalise(currentReference());
        String hypothesis = TranscriptNormaliser.normalise(transcript);
        return reference.equals(hypothesis)
                ? Outcome.ok()
                : Outcome.fail("no-exact-match", "Normalised transcript does not match reference");
    }

    private Outcome<Void> checkWer(String transcript) {
        double wer = WordErrorRate.compute(currentReference(), transcript);
        return wer <= tuning.maxWer()
                ? Outcome.ok()
                : Outcome.fail("wer-too-high", "WER %.3f exceeds %.3f".formatted(wer, tuning.maxWer()));
    }

    private Outcome<Void> checkCer(String transcript) {
        double cer = CharacterErrorRate.compute(currentReference(), transcript);
        return cer <= tuning.maxCer()
                ? Outcome.ok()
                : Outcome.fail("cer-too-high", "CER %.3f exceeds %.3f".formatted(cer, tuning.maxCer()));
    }

    private Outcome<Void> checkTokenF1(String transcript) {
        double f1 = TokenF1.compute(currentReference(), transcript);
        return f1 >= tuning.minTokenF1()
                ? Outcome.ok()
                : Outcome.fail("token-f1-too-low", "Token F1 %.3f below %.3f".formatted(f1, tuning.minTokenF1()));
    }

    // Skeletal: a single reference held from the last invoke. Replacing this
    // with per-sample reference plumbing is a meaningful contributor task.
    private volatile SttRequest lastRequest;

    private String currentReference() {
        String reference = lastRequest == null ? null : references.referenceFor(lastRequest);
        return reference == null ? "" : reference;
    }

    @Override
    public List<Covariate> covariates() {
        return List.of(Covariate.custom("stt_provider", CovariateCategory.CONFIGURATION));
    }

    @Override
    public Map<String, Supplier<String>> customCovariateResolvers() {
        return Map.of("stt_provider", provider::id);
    }

    @Override
    public String id() {
        return "stt-transcription";
    }

    @Override
    public Outcome<String> invoke(SttRequest request, TokenTracker tracker) {
        this.lastRequest = request;
        Outcome<SttResponse> response = provider.transcribe(request);
        return response.map(SttResponse::transcript);
    }

    /**
     * Acceptance thresholds the contract judges against.
     *
     * @param maxWer     maximum tolerated Word Error Rate
     * @param maxCer     maximum tolerated Character Error Rate
     * @param minTokenF1 minimum required Token F1
     */
    public record SttTuning(double maxWer, double maxCer, double minTokenF1) {

        /** A lenient default suitable for the scaffold's noop provider. */
        public static final SttTuning DEFAULT = new SttTuning(0.30, 0.20, 0.70);
    }
}
