package org.mavai.sttbench;

/**
 * Placeholder entry point for the STT benchmark.
 *
 * <p>The benchmark is currently driven through the Gradle tasks
 * {@code generateAudioVariants}, {@code runSttBenchmark}, and
 * {@code generateSttReport}. This class exists so the {@code application}
 * plugin has a {@code mainClass} and so a Hackergarten session has an
 * obvious home for a real command-line driver.
 */
public final class SttBenchCli {

    private SttBenchCli() {
    }

    /**
     * @param args ignored
     */
    public static void main(String[] args) {
        System.out.println("mavai-stt-bench — drive the benchmark via the Gradle tasks:");
        System.out.println("  ./gradlew generateAudioVariants");
        System.out.println("  ./gradlew runSttBenchmark");
        System.out.println("  ./gradlew generateSttReport");
    }
}
