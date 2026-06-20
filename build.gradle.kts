plugins {
    `java-library`
    application
    idea
}

group = "org.mavai"
version = "0.1.0-SNAPSHOT"

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

// Compile with -parameters so PUnit can inject use-case arguments by name.
tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

repositories {
    mavenCentral()
}

// ═══════════════════════════════════════════════════════════════════════════
// Dependencies
// ═══════════════════════════════════════════════════════════════════════════

dependencies {
    // PUnit author-facing API + engine. JUnit-free on the main classpath.
    implementation(libs.punit.core)

    // Outcome — expected-failure result type (provider transcription failures,
    // recipe/explore-result parse failures travel as data, not exceptions).
    implementation(libs.outcome)

    // Jackson — YAML recipes + JSON explore-result files.
    implementation(libs.jackson.databind)
    implementation(libs.jackson.yaml)

    // Nullability annotations.
    implementation(libs.jspecify)

    // Logging — Log4j2 over SLF4J, aligned with punitexamples.
    implementation(libs.log4j.api)
    runtimeOnly(libs.log4j.core)
    runtimeOnly(libs.log4j.slf4j2)

    // Test stack — JUnit 6 (deliberate; see libs.versions.toml).
    testImplementation(libs.punit.report)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.assertj.core)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}

application {
    // No real entry point yet — the benchmark is driven through the placeholder
    // Gradle tasks below. Pinned so `application` stays happy until a Hackergarten
    // session wires a CLI.
    mainClass.set("org.mavai.sttbench.SttBenchCli")
}

// ═══════════════════════════════════════════════════════════════════════════
// STT benchmark workflow — placeholder tasks
// ═══════════════════════════════════════════════════════════════════════════
//
// The intended pipeline is:
//
//     ./gradlew generateAudioVariants   # corpus × recipe  -> generated-audio/
//     ./gradlew runSttBenchmark         # PUnit explore over providers -> results/explore/
//     ./gradlew generateSttReport       # explore results -> reports/ (HTML + Markdown)
//
// These are intentionally incomplete. They print what they WILL do and where,
// so a contributor has an obvious extension point. Filling them in is a
// meaningful Hackergarten task.

tasks.register("generateAudioVariants") {
    group = "stt-bench"
    description = "Generates audio variants from corpus × recipe (placeholder)."
    doLast {
        logger.lifecycle("generateAudioVariants: placeholder.")
        logger.lifecycle("  Will read:  corpus/audio/*.wav + recipes/*.yml")
        logger.lifecycle("  Will write: generated-audio/<recipe-id>/<clip>__<recipe-id>.wav")
        logger.lifecycle("  See org.mavai.sttbench.recipe.* for the extension point.")
    }
}

tasks.register("runSttBenchmark") {
    group = "stt-bench"
    description = "Runs the PUnit STT benchmark across registered providers (placeholder)."
    doLast {
        logger.lifecycle("runSttBenchmark: placeholder.")
        logger.lifecycle("  Will read:  generated-audio/ + corpus/transcripts/ + a registered SttProvider")
        logger.lifecycle("  Will run:   a PUnit explore over the STT service contract")
        logger.lifecycle("  Will write: results/explore/<provider-id>/<recipe-id>.json")
        logger.lifecycle("  See org.mavai.sttbench.contract.SttServiceContract for the extension point.")
    }
}

tasks.register("generateSttReport") {
    group = "stt-bench"
    description = "Consolidates PUnit explore results into an STT comparison report (placeholder)."
    doLast {
        logger.lifecycle("generateSttReport: placeholder.")
        logger.lifecycle("  Will read:  results/explore/**/*.json")
        logger.lifecycle("  Will write: reports/stt-comparison.html and reports/stt-comparison.md")
        logger.lifecycle("  See org.mavai.sttbench.report.SttReportGenerator for the extension point.")
    }
}
