package org.mavai.sttbench.report;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

/**
 * A minimal projection of one PUnit explore-result file: the data the STT
 * comparison report needs for a single provider × recipe cell.
 *
 * <p>This is deliberately not a faithful model of PUnit's full explore-result
 * schema — the reporting layer is an STT-specific consumer, not a generic
 * PUnit reporting subsystem. It reads the handful of fields that distinguish
 * one provider/recipe run from another and ignores the rest (hence
 * {@link JsonIgnoreProperties}).
 *
 * <p>One instance describes one cell of the report's headline artifact: the
 * provider × recipe matrix whose cells are a <em>pass rate with a Wilson
 * confidence interval</em>. The pass rate is for a single named, soundly-judged
 * {@code criterion} (e.g. {@code normalised-exact-match}, or the P95 latency
 * percentile) — surfacing the interval, not just the point estimate, is the
 * whole point: it shows the statistical confidence behind each cell rather than
 * a bare average.
 *
 * <p>The mean WER/CER/Token-F1 ride alongside as <em>descriptive, deliberately
 * unjudged</em> columns. They are observed and reported, never thresholded into
 * a verdict: PUnit cannot yet characterise these continuous accuracy metrics
 * inferentially (see the orchestrator follow-up note
 * {@code stt-metric-characterisation-and-pooled-rate-archetype}), so presenting
 * them with a confidence interval or a pass/fail would overclaim. A bare
 * observed mean is the honest surface for them.
 *
 * <p>The field names here are the JSON contract that {@code runSttBenchmark}
 * writes and the report reads; they are no longer placeholders. Reporting
 * more than one criterion per cell is a deliberate extension, not a gap in
 * this record.
 *
 * @param providerId  id of the provider this run benchmarked
 * @param recipeId    id of the recipe applied to the audio
 * @param criterion   name of the criterion the {@code passRate} is measured
 *                    against (the cell's headline pass/fail rule)
 * @param passRate    observed pass rate for {@code criterion}, in {@code [0, 1]}
 * @param ciLow       lower bound of the Wilson confidence interval on
 *                    {@code passRate}, in {@code [0, 1]}
 * @param ciHigh      upper bound of the Wilson confidence interval on
 *                    {@code passRate}, in {@code [0, 1]}
 * @param sampleCount number of samples the explore run drew
 * @param meanWer     mean Word Error Rate across the run (descriptive, unjudged)
 * @param meanCer     mean Character Error Rate across the run (descriptive, unjudged)
 * @param meanTokenF1 mean Token F1 across the run (descriptive, unjudged)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ExploreResult(
        String providerId,
        String recipeId,
        String criterion,
        double passRate,
        double ciLow,
        double ciHigh,
        int sampleCount,
        double meanWer,
        double meanCer,
        double meanTokenF1) {

    public ExploreResult {
        Objects.requireNonNull(providerId, "providerId");
        Objects.requireNonNull(recipeId, "recipeId");
        Objects.requireNonNull(criterion, "criterion");
    }
}
