package dev.harrel.benchmark;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.harrel.jsonschema.Validator;
import dev.harrel.jsonschema.ValidatorFactory;
import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;

@State(Scope.Benchmark)
public class SpecificationBenchmark {
    @Param({"unused"})
    public String benchmarkFileName;

    private final URI schemaUri = URI.create("urn:bench");
    private Validator validator;
    private JsonNode instanceNode;

    @Setup
    public void setup() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode benchmarkNode = objectMapper.readTree(new File(benchmarkFileName));
        validator = new ValidatorFactory().createValidator();
        validator.registerSchema(schemaUri, benchmarkNode.get("schema"));
        instanceNode = benchmarkNode.get("instance");
    }

    @Benchmark
    public Validator.Result benchmark() {
        Validator.Result res = validator.validate(schemaUri, instanceNode);
        if (!res.isValid()) {
            throw new IllegalArgumentException("Validation failed: " + res.getErrors());
        }
        return res;
    }
}
