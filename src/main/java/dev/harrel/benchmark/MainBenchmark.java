package dev.harrel.benchmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.harrel.jsonschema.Validator;
import dev.harrel.jsonschema.ValidatorFactory;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1, warmups = 0)
@Warmup(iterations = 1, time = 2)
@Measurement(iterations = 3, time = 8)
@State(Scope.Benchmark)
public class MainBenchmark {
    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(MainBenchmark.class.getSimpleName())
                .param("name", "hola", "amigo")
                .resultFormat(ResultFormatType.JSON)
                .build();

        new Runner(opt).run();
    }

    @Param({"unused"})
    public String name;

    private URI schemaUri = URI.create("urn:bench");
    private Validator validator;
    private JsonNode schemaNode;
    private JsonNode instanceNode;

    @Setup
    public void setup() throws JsonProcessingException {
        System.out.println(name);
        ObjectMapper objectMapper = new ObjectMapper();
        schemaNode = objectMapper.readTree("""
                {
                  "type": "null"
                }""");
        instanceNode = objectMapper.readTree("null");
        validator = new ValidatorFactory().createValidator();
        validator.registerSchema(schemaUri, schemaNode);
    }

    @Benchmark
    public Validator.Result typeNull() {
        return validator.validate(schemaUri, instanceNode);
    }
}
