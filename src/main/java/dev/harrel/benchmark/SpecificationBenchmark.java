package dev.harrel.benchmark;

import dev.harrel.jsonschema.JsonNode;
import dev.harrel.jsonschema.JsonNodeFactory;
import dev.harrel.jsonschema.Validator;
import dev.harrel.jsonschema.ValidatorFactory;
import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

@State(Scope.Benchmark)
public class SpecificationBenchmark {
    @Param({"unused"})
    public String benchmarkFileName;

    private final URI schemaUri = URI.create("urn:bench");
    private Validator validator;
    private JsonNode instanceNode;

    @Setup
    public void setup() throws Exception {
        String providerFactoryName = System.getenv("JSON_PROVIDER_FACTORY");
        Class<?> factoryClass = Class.forName(providerFactoryName);
        JsonNodeFactory factory = (JsonNodeFactory) factoryClass.getConstructor().newInstance();
        JsonNode benchmarkNode = factory.create(Files.readString(Path.of(benchmarkFileName)));
        validator = new ValidatorFactory().createValidator();
        validator.registerSchema(schemaUri, benchmarkNode.asObject().get("schema"));
        instanceNode = benchmarkNode.asObject().get("instance");
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
