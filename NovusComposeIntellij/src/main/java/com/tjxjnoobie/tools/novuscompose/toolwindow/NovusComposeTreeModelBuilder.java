package com.tjxjnoobie.tools.novuscompose.toolwindow;

import com.tjxjnoobie.tools.novuscompose.analysis.CompositionModel.CompositionDiagnostic;
import com.tjxjnoobie.tools.novuscompose.analysis.CompositionModel.MethodModel;
import com.tjxjnoobie.tools.novuscompose.analysis.CompositionModel.MethodSignature;
import com.tjxjnoobie.tools.novuscompose.analysis.CompositionModel.ProjectCompositionState;
import com.tjxjnoobie.tools.novuscompose.analysis.CompositionModel.SourceInterfaceModel;
import com.tjxjnoobie.tools.novuscompose.analysis.CompositionModel.TargetComposition;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class NovusComposeTreeModelBuilder {
    private NovusComposeTreeModelBuilder() {
    }

    public static DefaultMutableTreeNode buildRoot(ProjectCompositionState state) {
        DefaultMutableTreeNode root = node("Novus Compose");
        root.add(buildDomainsNode(state));
        root.add(buildDependenciesNode(state));
        root.add(buildDiagnosticsNode(state));
        return root;
    }

    public static List<String> flatten(DefaultMutableTreeNode root) {
        List<String> lines = new ArrayList<>();
        collect(root, 0, lines);
        return lines;
    }

    private static DefaultMutableTreeNode buildDomainsNode(ProjectCompositionState state) {
        List<TargetComposition> targets = state.targets().values().stream()
                .sorted(Comparator.comparing(TargetComposition::targetQualifiedName))
                .toList();
        DefaultMutableTreeNode domainsNode = node(NodeKind.SECTION, "Generated Domains (" + targets.size() + ")");
        for (TargetComposition target : targets) {
            DefaultMutableTreeNode targetNode = node(new TypeNode(
                    NodeKind.DOMAIN,
                    target.targetQualifiedName() + " -> " + target.generatedQualifiedName(),
                    target.targetQualifiedName()));

            List<SourceInterfaceModel> sources = target.sources().stream()
                    .sorted(Comparator.comparing(SourceInterfaceModel::qualifiedName))
                    .toList();
            DefaultMutableTreeNode sourcesNode = node(NodeKind.SECTION, "Source Interfaces (" + sources.size() + ")");
            for (SourceInterfaceModel source : sources) {
                sourcesNode.add(node(new SourceInterfaceNode(
                        source.qualifiedName() + " via " + source.resolverName() + "()",
                        source.qualifiedName(),
                        source.resolverName())));
            }
            targetNode.add(sourcesNode);

            List<MethodModel> methods = target.methodsBySignature().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.comparing(MethodSignature::displayName)))
                    .flatMap(entry -> entry.getValue().stream()
                            .sorted(Comparator.comparing(MethodModel::ownerQualifiedName)
                                    .thenComparing(MethodModel::generatedMethodName)))
                    .toList();
            DefaultMutableTreeNode methodsNode = node(NodeKind.SECTION, "Generated Methods (" + methods.size() + ")");
            for (MethodModel method : methods) {
                methodsNode.add(node(new MethodTreeNode(
                        renderMethod(method),
                        method.ownerQualifiedName(),
                        method.declaredMethodName(),
                        List.copyOf(method.parameterTypes()))));
            }
            targetNode.add(methodsNode);

            List<Map.Entry<String, List<MethodModel>>> collisions = target.methodsByName().entrySet().stream()
                    .filter(entry -> entry.getValue().size() > 1)
                    .sorted(Map.Entry.comparingByKey())
                    .toList();
            if (!collisions.isEmpty()) {
                DefaultMutableTreeNode collisionsNode = node(NodeKind.COLLISION_GROUP, "Name Collisions (" + collisions.size() + ")");
                for (Map.Entry<String, List<MethodModel>> collision : collisions) {
                    DefaultMutableTreeNode collisionNode = node(NodeKind.COLLISION_GROUP, collision.getKey());
                    collision.getValue().stream()
                            .sorted(Comparator.comparing(MethodModel::ownerQualifiedName))
                            .forEach(method -> collisionNode.add(node(new MethodTreeNode(
                                    renderMethod(method),
                                    method.ownerQualifiedName(),
                                    method.declaredMethodName(),
                                    List.copyOf(method.parameterTypes())))));
                    collisionsNode.add(collisionNode);
                }
                targetNode.add(collisionsNode);
            }

            domainsNode.add(targetNode);
        }
        return domainsNode;
    }

    private static DefaultMutableTreeNode buildDependenciesNode(ProjectCompositionState state) {
        Map<String, DependencyView> dependencies = new LinkedHashMap<>();
        for (TargetComposition target : state.targets().values()) {
            for (SourceInterfaceModel source : target.sources()) {
                dependencies.computeIfAbsent(
                                source.qualifiedName(),
                                ignored -> new DependencyView(source.qualifiedName(), source.resolverName()))
                        .targets()
                        .add(target.targetQualifiedName());
            }
        }

        DefaultMutableTreeNode dependenciesNode = node(NodeKind.SECTION, "Dependency Interfaces (" + dependencies.size() + ")");
        dependencies.values().stream()
                .sorted(Comparator.comparing(DependencyView::qualifiedName))
                .forEach(dependency -> {
                    DefaultMutableTreeNode dependencyNode = node(new SourceInterfaceNode(
                            dependency.qualifiedName() + " via " + dependency.resolverName() + "()",
                            dependency.qualifiedName(),
                            dependency.resolverName()));
                    DefaultMutableTreeNode targetsNode = node(NodeKind.SECTION, "Composes To (" + dependency.targets().size() + ")");
                    dependency.targets().stream().sorted().forEach(target ->
                            targetsNode.add(node(new TypeNode(NodeKind.DOMAIN_TARGET, target, target))));
                    dependencyNode.add(targetsNode);

                    List<CompositionDiagnostic> diagnostics = state.diagnosticsBySource()
                            .getOrDefault(dependency.qualifiedName(), List.of());
                    if (!diagnostics.isEmpty()) {
                        DefaultMutableTreeNode diagnosticsNode = node(NodeKind.DIAGNOSTIC_GROUP, "Diagnostics (" + diagnostics.size() + ")");
                        diagnostics.stream()
                                .sorted(Comparator.comparing(
                                                (CompositionDiagnostic diagnostic) -> diagnostic.severity().toString())
                                        .thenComparing(CompositionDiagnostic::message))
                                .forEach(diagnostic -> diagnosticsNode.add(node(new DiagnosticTreeNode(
                                        "[" + diagnostic.severity() + "] " + diagnostic.message(),
                                        diagnostic.message(),
                                        dependency.qualifiedName(),
                                        diagnostic.severity().toString().equals("ERROR")
                                                ? NodeKind.DIAGNOSTIC_ERROR
                                                : NodeKind.DIAGNOSTIC_WARNING))));
                        dependencyNode.add(diagnosticsNode);
                    }

                    dependenciesNode.add(dependencyNode);
                });
        return dependenciesNode;
    }

    private static DefaultMutableTreeNode buildDiagnosticsNode(ProjectCompositionState state) {
        Collection<List<CompositionDiagnostic>> allDiagnostics = state.diagnosticsBySource().values();
        int diagnosticCount = allDiagnostics.stream().mapToInt(List::size).sum();
        DefaultMutableTreeNode diagnosticsNode = node(NodeKind.DIAGNOSTIC_GROUP, "Diagnostics (" + diagnosticCount + ")");

        state.diagnosticsBySource().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    DefaultMutableTreeNode sourceNode = node(new TypeNode(
                            NodeKind.DEPENDENCY,
                            entry.getKey(),
                            entry.getKey()));
                    entry.getValue().stream()
                            .sorted(Comparator.comparing(
                                            (CompositionDiagnostic diagnostic) -> diagnostic.severity().toString())
                                    .thenComparing(CompositionDiagnostic::message))
                            .forEach(diagnostic -> sourceNode.add(node(new DiagnosticTreeNode(
                                    "[" + diagnostic.severity() + "] " + diagnostic.message(),
                                    diagnostic.message(),
                                    entry.getKey(),
                                    diagnostic.severity().toString().equals("ERROR")
                                            ? NodeKind.DIAGNOSTIC_ERROR
                                            : NodeKind.DIAGNOSTIC_WARNING))));
                    diagnosticsNode.add(sourceNode);
                });
        return diagnosticsNode;
    }

    private static String renderMethod(MethodModel method) {
        String parameters = String.join(", ", method.parameterTypes());
        String throwsSuffix = method.thrownTypes().isEmpty()
                ? ""
                : " throws " + String.join(", ", method.thrownTypes());
        String ownerSuffix = method.generatedMethodName().equals(method.declaredMethodName())
                ? method.ownerQualifiedName()
                : method.ownerQualifiedName() + "#" + method.declaredMethodName();
        return method.generatedMethodName() + "(" + parameters + ") -> " + method.returnType()
                + " [" + ownerSuffix + "]" + throwsSuffix;
    }

    private static void collect(DefaultMutableTreeNode node, int depth, List<String> lines) {
        Object value = node.getUserObject();
        lines.add("  ".repeat(Math.max(0, depth)) + labelFor(value));
        for (int index = 0; index < node.getChildCount(); index++) {
            collect((DefaultMutableTreeNode) node.getChildAt(index), depth + 1, lines);
        }
    }

    private static String labelFor(Object value) {
        if (value instanceof TreeNodeValue treeNodeValue) {
            return treeNodeValue.label();
        }
        return String.valueOf(value);
    }

    private static DefaultMutableTreeNode node(String label) {
        return node(NodeKind.SECTION, label);
    }

    private static DefaultMutableTreeNode node(NodeKind kind, String label) {
        return new DefaultMutableTreeNode(new TextTreeNode(kind, label));
    }

    private static DefaultMutableTreeNode node(TreeNodeValue value) {
        return new DefaultMutableTreeNode(value);
    }

    private record DependencyView(
            String qualifiedName,
            String resolverName,
            Set<String> targets) {
        private DependencyView(String qualifiedName, String resolverName) {
            this(qualifiedName, resolverName, new LinkedHashSet<>());
        }
    }

    public enum NodeKind {
        ROOT,
        SECTION,
        DOMAIN,
        DOMAIN_TARGET,
        DEPENDENCY,
        METHOD,
        COLLISION_GROUP,
        DIAGNOSTIC_GROUP,
        DIAGNOSTIC_WARNING,
        DIAGNOSTIC_ERROR
    }

    public sealed interface TreeNodeValue permits TextTreeNode, TypeNode, SourceInterfaceNode, MethodTreeNode, DiagnosticTreeNode {
        NodeKind kind();

        String label();
    }

    public record TextTreeNode(NodeKind kind, String label) implements TreeNodeValue {
        @Override
        public String toString() {
            return label;
        }
    }

    public record TypeNode(NodeKind kind, String label, String qualifiedName) implements TreeNodeValue {
        @Override
        public String toString() {
            return label;
        }
    }

    public record SourceInterfaceNode(String label, String qualifiedName, String resolverName) implements TreeNodeValue {
        @Override
        public NodeKind kind() {
            return NodeKind.DEPENDENCY;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public record MethodTreeNode(
            String label,
            String ownerQualifiedName,
            String methodName,
            List<String> parameterTypes) implements TreeNodeValue {
        @Override
        public NodeKind kind() {
            return NodeKind.METHOD;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public record DiagnosticTreeNode(
            String label,
            String diagnosticMessage,
            String sourceQualifiedName,
            NodeKind kind) implements TreeNodeValue {
        @Override
        public String toString() {
            return label;
        }
    }
}
