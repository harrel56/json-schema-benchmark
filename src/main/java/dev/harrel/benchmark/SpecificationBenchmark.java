package dev.harrel.benchmark;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.harrel.jsonschema.JsonNode;
import dev.harrel.jsonschema.JsonNodeFactory;
import dev.harrel.jsonschema.Validator;
import dev.harrel.jsonschema.ValidatorFactory;
import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.net.URI;

@State(Scope.Benchmark)
public class SpecificationBenchmark {
    @Param({"unused"})
    public String benchmarkFileName;

    private final URI schemaUri = URI.create("urn:bench");
    private Validator validator;
    private String instanceString;

    @Setup
    public void setup() throws Exception {
        String providerFactoryName = System.getenv("JSON_PROVIDER_FACTORY");
        Class<?> factoryClass = Class.forName(providerFactoryName);
        JsonNodeFactory factory = (JsonNodeFactory) factoryClass.getConstructor().newInstance();
        com.fasterxml.jackson.databind.JsonNode jacksonNode = new ObjectMapper().readTree(new File(benchmarkFileName));
        validator = new ValidatorFactory().withJsonNodeFactory(factory).createValidator();
        validator.registerSchema(schemaUri, jacksonNode.get("schema").toString());
        instanceString = jacksonNode.get("instance").toString();
    }

    @Benchmark
    public Validator.Result benchmark() {
        Validator.Result res = validator.validate(schemaUri, instanceString);
        if (!res.isValid()) {
            throw new IllegalArgumentException("Validation failed: " + res.getErrors());
        }
        return res;
    }
}
