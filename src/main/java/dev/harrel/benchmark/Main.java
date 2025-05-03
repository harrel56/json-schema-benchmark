package dev.harrel.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {
        String ver = Objects.requireNonNull(System.getenv("LIB_VERSION"));
        String jsonProvider = Objects.requireNonNull(System.getenv("JSON_PROVIDER"));
        Path outputPath = Path.of("build/reports/jmh/%s/%s/jmh-result.json".formatted(ver, jsonProvider));
        Files.createDirectories(outputPath.getParent());

        Options opt = new OptionsBuilder()
                .include(SpecificationBenchmark.class.getSimpleName())
                .param("name", "hola", "amigo")
                .result(outputPath.toString())
                .resultFormat(ResultFormatType.JSON)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .forks(1)
                .warmupForks(0)
                .warmupIterations(1)
                .warmupTime(TimeValue.seconds(2))
                .measurementIterations(3)
                .measurementTime(TimeValue.seconds(10))
                .build();

        new Runner(opt).run();
    }
}
