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
 * <p><strong>Skeletal.</strong> Both renderers below produce a correct but
 * bare table. Styling the HTML, adding the WER/CER/F1 columns, and sorting /
 * highlighting the best provider per recipe are obvious Hackergarten
 * extensions. The file-discovery and file-writing wiring lives in
 * {@code generateSttReport}.
 */
public final class SttReportGenerator {

    /**
     * Renders the comparison as a Markdown document.
     *
     * @param results the explore results to consolidate
     * @return a Markdown report
     */
    public String renderMarkdown(List<ExploreResult> results) {
        StringBuilder md = new StringBuilder();
        md.append("# STT Comparison Report\n\n");
        md.append("| Provider | Recipe | Pass rate | Samples |\n");
        md.append("|----------|--------|-----------|---------|\n");
        for (ExploreResult r : results) {
            md.append("| %s | %s | %.3f | %d |%n"
                    .formatted(r.providerId(), r.recipeId(), r.passRate(), r.sampleCount()));
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
                .map(r -> "    <tr><td>%s</td><td>%s</td><td>%.3f</td><td>%d</td></tr>"
                        .formatted(r.providerId(), r.recipeId(), r.passRate(), r.sampleCount()))
                .collect(Collectors.joining("\n"));
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head><meta charset="utf-8"><title>STT Comparison Report</title></head>
                <body>
                <h1>STT Comparison Report</h1>
                <table border="1">
                  <thead>
                    <tr><th>Provider</th><th>Recipe</th><th>Pass rate</th><th>Samples</th></tr>
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
