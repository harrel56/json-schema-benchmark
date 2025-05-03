package dev.harrel.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {
        String ver = Objects.requireNonNull(System.getenv("LIB_VERSION"));
        String jsonProvider = Objects.requireNonNull(System.getenv("JSON_PROVIDER"));
        Path outputPath = Path.of("build/reports/jmh/%s/%s/jmh-result.json".formatted(ver, jsonProvider));
        Files.createDirectories(outputPath.getParent());

        String[] benchmarkFileNames = computeBenchmarkFileNames();
        System.out.printf("Found %d benchmark files: %s%n", benchmarkFileNames.length, Arrays.toString(benchmarkFileNames));

        ChainedOptionsBuilder opt = new OptionsBuilder()
                .include(SpecificationBenchmark.class.getSimpleName())
                .param("benchmarkFileName", benchmarkFileNames)
                .result(outputPath.toString())
                .resultFormat(ResultFormatType.JSON)
                .timeUnit(TimeUnit.MICROSECONDS)
                .forks(1)
                .warmupForks(0)
                .warmupIterations(1)
                .warmupTime(TimeValue.seconds(2))
                .measurementIterations(3)
                .measurementTime(TimeValue.seconds(10));
        String testRun = System.getenv("TEST_RUN");
        if (testRun != null && !"0".equals(testRun)) {
            opt = opt.mode(Mode.SingleShotTime);
        } else {
            opt = opt.mode(Mode.AverageTime);
        }

        new Runner(opt.build()).run();
    }

    private static String[] computeBenchmarkFileNames() throws URISyntaxException, IOException {
        URL benchmarksDirUrl = Objects.requireNonNull(Main.class.getResource("/benchmarks"));
        File file = new File(benchmarksDirUrl.toURI());
        List<String> benchmarkFiles = new ArrayList<>();
        Files.walkFileTree(file.toPath(), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                benchmarkFiles.add(file.toString());
                return FileVisitResult.CONTINUE;
            }
        });
        return benchmarkFiles.toArray(new String[0]);
    }
}
