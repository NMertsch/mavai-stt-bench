package org.mavai.sttbench.report;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Consolidates multiple PUnit explore results into a human-readable STT
 * comparison report.
 *
 * <p>The scope is narrow on purpose: this consumes explore results and emits
 * an STT-shaped comparison (provider × recipe, with pass rates). It does not
 * try to become a generic PUnit reporting subsystem — for full PUnit
 * reporting, use {@code punit-report}.
 *
 * <p>The headline cell is a soundly-judged pass rate with its confidence
 * interval. The WER/CER/F1 columns are <em>observed</em> means — descriptive
 * only, never judged (see {@link ExploreResult}) — and are labelled
 * {@code (obs)} to keep that distinction visible to a reader scanning the
 * table.
 *
 * <p><strong>Skeletal.</strong> Both renderers below produce a correct but
 * bare <em>flat</em> table — one row per {@link ExploreResult}, with the pass
 * rate, its Wilson confidence interval, and the mean metrics. The report's
 * decided headline artifact is a provider × recipe <em>matrix</em> with
 * colour-graded cells; pivoting this flat table into that matrix, colour-coding
 * by pass rate, and drawing the confidence interval are the Hackergarten
 * reporting tasks. The file-discovery and file-writing wiring lives in
 * {@code generateSttReport}.
 */
public final class SttReportGenerator {

    private static String ci(ExploreResult r) {
        return "%.3f [%.3f, %.3f]".formatted(r.passRate(), r.ciLow(), r.ciHigh());
    }

    /**
     * Renders the comparison as a Markdown document.
     *
     * @param results the explore results to consolidate
     * @return a Markdown report
     */
    public String renderMarkdown(List<ExploreResult> results) {
        StringBuilder md = new StringBuilder();
        md.append("# STT Comparison Report\n\n");
        md.append("_Pass rate is judged (Wilson CI). WER/CER/F1 are observed means — "
                + "descriptive only, not judged._\n\n");
        md.append("| Provider | Recipe | Criterion | Pass rate [95% CI] | Samples "
                + "| WER (obs) | CER (obs) | F1 (obs) |\n");
        md.append("|----------|--------|-----------|--------------------|---------"
                + "|-----------|-----------|----------|\n");
        for (ExploreResult r : results) {
            md.append("| %s | %s | %s | %s | %d | %.3f | %.3f | %.3f |%n".formatted(
                    r.providerId(), r.recipeId(), r.criterion(), ci(r), r.sampleCount(),
                    r.meanWer(), r.meanCer(), r.meanTokenF1()));
        }
        return md.toString();
    }

    /**
     * Renders the comparison as a standalone HTML document.
     *
     * @param results the explore results to consolidate
     * @return an HTML report
     */
    public String renderHtml(List<ExploreResult> results) {
        String rows = results.stream()
                .map(r -> ("    <tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td>"
                        + "<td>%d</td><td>%.3f</td><td>%.3f</td><td>%.3f</td></tr>").formatted(
                        r.providerId(), r.recipeId(), r.criterion(), ci(r), r.sampleCount(),
                        r.meanWer(), r.meanCer(), r.meanTokenF1()))
                .collect(Collectors.joining("\n"));
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head><meta charset="utf-8"><title>STT Comparison Report</title></head>
                <body>
                <h1>STT Comparison Report</h1>
                <p><em>Pass rate is judged (Wilson CI). WER/CER/F1 are observed means \
                &mdash; descriptive only, not judged.</em></p>
                <table border="1">
                  <thead>
                    <tr><th>Provider</th><th>Recipe</th><th>Criterion</th><th>Pass rate [95% CI]</th>\
                <th>Samples</th><th>WER (obs)</th><th>CER (obs)</th><th>F1 (obs)</th></tr>
                  </thead>
                  <tbody>
                %s
                  </tbody>
                </table>
                </body>
                </html>
                """.formatted(rows);
    }
}
