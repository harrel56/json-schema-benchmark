package org.openjdk.jmh.results.format;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.openjdk.jmh.infra.BenchmarkParams;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.openjdk.jmh.results.format.JSONResultFormat.toJsonString;

public class ByteBuddyUtil {
    public static void overrideJsonFormat() throws ClassNotFoundException {
        ByteBuddyAgent.install();
        new ByteBuddy()
                .redefine(Class.forName("org.openjdk.jmh.results.format.JSONResultFormat"))
                .method(ElementMatchers.named("emitParams"))
                .intercept(MethodDelegation.to(ByteBuddyUtil.class))
                .make()
                .load(ByteBuddyUtil.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }

    @Advice.OnMethodEnter
    public static String emitParams(BenchmarkParams params) {
        StringBuilder sb = new StringBuilder();
        for (String k : params.getParamsKeys()) {
            sb.append("\"").append(k).append("\" : ");
            sb.append(toJsonString(params.getParam(k))).append(",\n");
        }

        String benchmarkFileName = params.getParam("benchmarkFileName");
        if (benchmarkFileName == null) {
            throw new IllegalArgumentException("Param 'benchmarkFileName' is required");
        }
        try {
            String model = Files.readString(Path.of(benchmarkFileName));
            sb.append("\"model\": ").append(model);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return sb.toString();
    }
}
