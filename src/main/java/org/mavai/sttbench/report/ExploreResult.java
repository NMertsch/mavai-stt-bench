package org.mavai.sttbench.report;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A minimal projection of one PUnit explore-result file, carrying only the
 * fields the STT comparison report needs.
 *
 * <p>This is deliberately not a faithful model of PUnit's full explore-result
 * schema — the reporting layer is an STT-specific consumer, not a generic
 * PUnit reporting subsystem. It reads the handful of fields that distinguish
 * one provider/recipe run from another and ignores the rest.
 *
 * <p><strong>Skeletal.</strong> The exact field names are placeholders to be
 * reconciled against a real explore-result file produced by
 * {@code runSttBenchmark}. Reconciling them is a Hackergarten task.
 *
 * @param providerId   id of the provider this run benchmarked
 * @param recipeId     id of the recipe applied to the audio
 * @param passRate     overall pass rate the explore run observed
 * @param sampleCount  number of samples in the run
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ExploreResult(String providerId, String recipeId, double passRate, int sampleCount) {
}
