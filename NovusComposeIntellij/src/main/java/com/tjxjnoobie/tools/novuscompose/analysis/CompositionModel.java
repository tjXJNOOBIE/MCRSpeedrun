package com.tjxjnoobie.tools.novuscompose.analysis;

import com.intellij.lang.annotation.HighlightSeverity;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CompositionModel {
    public record ProjectCompositionState(
            Map<String, TargetComposition> targets,
            Map<String, List<CompositionDiagnostic>> diagnosticsBySource,
            Map<String, Set<String>> graph) {
    }

    public record TargetComposition(
            String targetQualifiedName,
            String targetSimpleName,
            String packageName,
            String moduleName,
            List<SourceInterfaceModel> sources,
            Map<MethodSignature, List<MethodModel>> methodsBySignature,
            Map<String, List<MethodModel>> methodsByName) {
        public String generatedSimpleName() {
            return targetSimpleName + "Generated";
        }

        public String generatedQualifiedName() {
            return packageName.isBlank() ? generatedSimpleName() : packageName + "." + generatedSimpleName();
        }
    }

    public record SourceInterfaceModel(
            String qualifiedName,
            String resolverName) {
    }

    public record MethodModel(
            String ownerQualifiedName,
            String resolverName,
            String methodName,
            String returnType,
            List<String> parameterTypes,
            List<String> thrownTypes) {
    }

    public record MethodSignature(
            String methodName,
            List<String> parameterTypes,
            String returnType) {
        public String displayName() {
            return methodName + "(" + String.join(", ", parameterTypes) + ")";
        }
    }

    public record CompositionDiagnostic(
            HighlightSeverity severity,
            String message) {
    }
}
