package com.tjxjnoobie.tools.novuscompose.analysis;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import com.tjxjnoobie.tools.novuscompose.analysis.CompositionModel.CompositionDiagnostic;
import com.tjxjnoobie.tools.novuscompose.analysis.CompositionModel.MethodModel;
import com.tjxjnoobie.tools.novuscompose.analysis.CompositionModel.MethodSignature;
import com.tjxjnoobie.tools.novuscompose.analysis.CompositionModel.ProjectCompositionState;
import com.tjxjnoobie.tools.novuscompose.analysis.CompositionModel.SourceInterfaceModel;
import com.tjxjnoobie.tools.novuscompose.analysis.CompositionModel.TargetComposition;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class CompositionProjectAnalyzer {
    private CompositionProjectAnalyzer() {
    }

    public static @NotNull ProjectCompositionState analyze(Project project) {
        PsiClass annotationClass = JavaPsiFacade.getInstance(project)
                .findClass(CompositionConstants.ANNOTATION_FQCN, GlobalSearchScope.projectScope(project));
        if (annotationClass == null) {
            return new ProjectCompositionState(Map.of(), Map.of(), Map.of());
        }

        Map<String, TargetBuilder> targetBuilders = new LinkedHashMap<>();
        Map<String, List<CompositionDiagnostic>> diagnosticsBySource = new LinkedHashMap<>();
        DefaultDirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        Collection<PsiClass> annotatedInterfaces =
                AnnotatedElementsSearch.searchPsiClasses(annotationClass, GlobalSearchScope.projectScope(project)).findAll();

        for (PsiClass sourceInterface : annotatedInterfaces) {
            if (!sourceInterface.isInterface() || sourceInterface.getQualifiedName() == null) {
                continue;
            }

            String sourceQualifiedName = sourceInterface.getQualifiedName();
            graph.addVertex(sourceQualifiedName);

            for (PsiClass targetInterface : extractTargets(sourceInterface)) {
                if (targetInterface.getQualifiedName() == null) {
                    continue;
                }

                String targetQualifiedName = targetInterface.getQualifiedName();
                graph.addVertex(targetQualifiedName);
                if (!graph.containsEdge(sourceQualifiedName, targetQualifiedName)) {
                    graph.addEdge(sourceQualifiedName, targetQualifiedName);
                }

                targetBuilders.computeIfAbsent(
                                targetQualifiedName,
                                ignored -> new TargetBuilder(targetInterface))
                        .addSource(sourceInterface);
            }
        }

        detectCycles(graph, diagnosticsBySource);

        Map<String, TargetComposition> targets = new LinkedHashMap<>();
        for (TargetBuilder builder : targetBuilders.values()) {
            TargetComposition target = builder.build();
            targets.put(target.targetQualifiedName(), target);

            for (Map.Entry<String, List<MethodModel>> entry : target.methodsByName().entrySet()) {
                if (entry.getValue().size() <= 1) {
                    continue;
                }
                String owners = entry.getValue().stream()
                        .sorted(java.util.Comparator.comparing(MethodModel::ownerQualifiedName))
                        .map(methodModel -> methodModel.ownerQualifiedName() + "#" + signatureFor(methodModel).displayName())
                        .collect(Collectors.joining("; "));
                for (MethodModel methodModel : entry.getValue()) {
                    diagnosticsBySource.computeIfAbsent(methodModel.ownerQualifiedName(), ignored -> new ArrayList<>())
                            .add(new CompositionDiagnostic(
                                    HighlightSeverity.WEAK_WARNING,
                                    "Multiple generated methods named '" + entry.getKey() + "' compose into "
                                            + target.targetQualifiedName() + ": " + owners));
                }
            }
        }

        return new ProjectCompositionState(targets, diagnosticsBySource, toAdjacencyMap(graph));
    }

    private static List<PsiClass> extractTargets(PsiClass sourceInterface) {
        return readAnnotationMetadata(sourceInterface).targets();
    }

    private static String extractMethodPrefix(PsiClass sourceInterface) {
        return readAnnotationMetadata(sourceInterface).methodPrefix();
    }

    private static AnnotationMetadata readAnnotationMetadata(PsiClass sourceInterface) {
        PsiAnnotation annotation = sourceInterface.getAnnotation(CompositionConstants.ANNOTATION_FQCN);
        if (annotation == null) {
            return new AnnotationMetadata(List.of(), "");
        }

        List<PsiClass> targets = new ArrayList<>();
        String methodPrefix = "";
        PsiAnnotationMemberValue attributeValue = annotation.findAttributeValue("value");
        if (attributeValue != null) {
            if (attributeValue instanceof com.intellij.psi.PsiArrayInitializerMemberValue arrayValue) {
                for (PsiAnnotationMemberValue initializer : arrayValue.getInitializers()) {
                    addTargetClass(initializer, targets);
                }
            } else {
                addTargetClass(attributeValue, targets);
            }
        }

        PsiAnnotationMemberValue prefixValue = annotation.findAttributeValue("methodPrefix");
        if (prefixValue instanceof com.intellij.psi.PsiLiteralExpression literalExpression) {
            Object value = literalExpression.getValue();
            if (value instanceof String prefix) {
                methodPrefix = prefix;
            }
        }
        return new AnnotationMetadata(targets, methodPrefix);
    }

    private static void addTargetClass(PsiAnnotationMemberValue memberValue, List<PsiClass> targets) {
        if (!(memberValue instanceof PsiClassObjectAccessExpression classObjectAccessExpression)) {
            return;
        }
        PsiType operand = classObjectAccessExpression.getOperand().getType();
        if (operand instanceof com.intellij.psi.PsiClassType classType) {
            PsiClass resolved = classType.resolve();
            if (resolved != null) {
                targets.add(resolved);
            }
        }
    }

    private static void detectCycles(
            DefaultDirectedGraph<String, DefaultEdge> graph,
            Map<String, List<CompositionDiagnostic>> diagnosticsBySource) {
        for (Set<String> stronglyConnectedSet : new KosarajuStrongConnectivityInspector<>(graph).stronglyConnectedSets()) {
            if (!isCycleComponent(graph, stronglyConnectedSet)) {
                continue;
            }
            List<String> cyclePath = stronglyConnectedSet.stream().sorted().collect(Collectors.toCollection(ArrayList::new));
            cyclePath.add(cyclePath.get(0));
            for (String node : stronglyConnectedSet) {
                diagnosticsBySource.computeIfAbsent(node, ignored -> new ArrayList<>())
                        .add(new CompositionDiagnostic(
                                HighlightSeverity.ERROR,
                                "Detected composed interface cycle: " + String.join(" -> ", cyclePath)));
            }
        }
    }

    private static MethodSignature signatureFor(MethodModel methodModel) {
        return new MethodSignature(methodModel.generatedMethodName(), methodModel.parameterTypes(), methodModel.returnType());
    }

    private static String resolverNameFor(String interfaceName, Map<String, Integer> resolverNameCounts) {
        String stripped = stripInterfacePrefix(interfaceName == null ? "dependency" : interfaceName);
        String baseName = "get" + stripped;
        int count = resolverNameCounts.merge(baseName, 1, Integer::sum);
        return count == 1 ? baseName : baseName + count;
    }

    private static String stripInterfacePrefix(String simpleName) {
        if (simpleName.length() > 1 && simpleName.startsWith("I") && Character.isUpperCase(simpleName.charAt(1))) {
            return simpleName.substring(1);
        }
        return simpleName;
    }

    private static String lowerCamel(String value) {
        if (value.isEmpty()) {
            return value;
        }
        if (value.length() == 1) {
            return value.toLowerCase(Locale.ROOT);
        }
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }

    private static boolean isCycleComponent(DefaultDirectedGraph<String, DefaultEdge> graph, Set<String> stronglyConnectedSet) {
        if (stronglyConnectedSet.size() > 1) {
            return true;
        }
        if (stronglyConnectedSet.isEmpty()) {
            return false;
        }
        String onlyVertex = stronglyConnectedSet.iterator().next();
        return graph.containsEdge(onlyVertex, onlyVertex);
    }

    private static Map<String, Set<String>> toAdjacencyMap(DefaultDirectedGraph<String, DefaultEdge> graph) {
        Map<String, Set<String>> adjacency = new LinkedHashMap<>();
        for (String vertex : graph.vertexSet()) {
            Set<String> targets = graph.outgoingEdgesOf(vertex).stream()
                    .map(graph::getEdgeTarget)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            adjacency.put(vertex, targets);
        }
        return adjacency;
    }

    private static final class TargetBuilder {
        private final PsiClass targetInterface;
        private final LinkedHashMap<String, SourceInterfaceModel> sources = new LinkedHashMap<>();
        private final LinkedHashMap<MethodSignature, List<MethodModel>> methodsBySignature = new LinkedHashMap<>();
        private final LinkedHashMap<String, List<MethodModel>> methodsByName = new LinkedHashMap<>();
        private final HashMap<String, Integer> resolverNameCounts = new HashMap<>();

        private TargetBuilder(PsiClass targetInterface) {
            this.targetInterface = targetInterface;
        }

        private void addSource(PsiClass sourceInterface) {
            String qualifiedName = Objects.requireNonNull(sourceInterface.getQualifiedName());
            if (sources.containsKey(qualifiedName)) {
                return;
            }

            SourceInterfaceModel sourceModel = new SourceInterfaceModel(
                    qualifiedName,
                    resolverNameFor(sourceInterface.getName(), resolverNameCounts),
                    normalizeMethodPrefix(extractMethodPrefix(sourceInterface)));
            sources.put(qualifiedName, sourceModel);

            for (PsiMethod method : sourceInterface.getAllMethods()) {
                if (!shouldCompose(method)) {
                    continue;
                }

                String declaredMethodName = method.getName();
                MethodModel methodModel = new MethodModel(
                        qualifiedName,
                        sourceModel.resolverName(),
                        declaredMethodName,
                        generatedMethodName(sourceModel.methodPrefix(), declaredMethodName),
                        method.getReturnType() == null ? "void" : method.getReturnType().getCanonicalText(),
                        java.util.Arrays.stream(method.getParameterList().getParameters())
                                .map(parameter -> parameter.getType().getCanonicalText())
                                .toList(),
                        java.util.Arrays.stream(method.getThrowsList().getReferencedTypes())
                                .map(PsiType::getCanonicalText)
                                .toList());
                MethodSignature signature = signatureFor(methodModel);
                methodsBySignature.computeIfAbsent(signature, ignored -> new ArrayList<>()).add(methodModel);
                methodsByName.computeIfAbsent(methodModel.generatedMethodName(), ignored -> new ArrayList<>()).add(methodModel);
            }
        }

        private boolean shouldCompose(PsiMethod method) {
            if (method.isConstructor()) {
                return false;
            }
            if (method.hasModifierProperty(PsiModifier.STATIC)) {
                return false;
            }
            if (!method.hasModifierProperty(PsiModifier.PUBLIC)) {
                return false;
            }
            PsiClass owner = method.getContainingClass();
            return owner == null || !"java.lang.Object".equals(owner.getQualifiedName());
        }

        private TargetComposition build() {
            Module module = ModuleUtilCore.findModuleForPsiElement(targetInterface);
            String packageName = targetInterface.getContainingFile() instanceof com.intellij.psi.PsiJavaFile javaFile
                    ? javaFile.getPackageName()
                    : "";
            return new TargetComposition(
                    Objects.requireNonNull(targetInterface.getQualifiedName()),
                    Objects.requireNonNull(targetInterface.getName()),
                    packageName,
                    module == null ? "project" : module.getName(),
                    List.copyOf(sources.values()),
                    methodsBySignature,
                    methodsByName);
        }

        private String normalizeMethodPrefix(String methodPrefix) {
            return methodPrefix == null ? "" : methodPrefix.trim();
        }

        private String generatedMethodName(String methodPrefix, String declaredMethodName) {
            if (methodPrefix.isBlank()) {
                return declaredMethodName;
            }
            return lowerCamel(methodPrefix) + Character.toUpperCase(declaredMethodName.charAt(0)) + declaredMethodName.substring(1);
        }
    }

    private record AnnotationMetadata(List<PsiClass> targets, String methodPrefix) {
    }
}
