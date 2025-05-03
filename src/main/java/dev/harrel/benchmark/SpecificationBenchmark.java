package dev.harrel.benchmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.harrel.jsonschema.Validator;
import dev.harrel.jsonschema.ValidatorFactory;
import org.openjdk.jmh.annotations.*;

import java.net.URI;

@State(Scope.Benchmark)
public class SpecificationBenchmark {
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
