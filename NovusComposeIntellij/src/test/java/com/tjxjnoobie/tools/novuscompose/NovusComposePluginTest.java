package com.tjxjnoobie.tools.novuscompose;

import com.intellij.ide.plugins.DynamicPlugins;
import com.intellij.ide.plugins.IdeaPluginDescriptorImpl;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.platform.testFramework.DynamicPluginTestUtilsKt;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.tjxjnoobie.tools.novuscompose.analysis.CompositionProjectAnalyzer;
import com.tjxjnoobie.tools.novuscompose.inspection.ComposesToInterfaceInspection;
import com.tjxjnoobie.tools.novuscompose.service.NovusComposeProjectService;
import com.tjxjnoobie.tools.novuscompose.toolwindow.NovusComposeNavigationSupport;
import com.tjxjnoobie.tools.novuscompose.toolwindow.NovusComposeQuickFixes;
import com.tjxjnoobie.tools.novuscompose.toolwindow.NovusComposeToolWindowFactory;
import com.tjxjnoobie.tools.novuscompose.toolwindow.NovusComposeToolWindowPanel;
import com.tjxjnoobie.tools.novuscompose.toolwindow.NovusComposeTreeCellRenderer;
import com.tjxjnoobie.tools.novuscompose.toolwindow.NovusComposeTreeModelBuilder;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class NovusComposePluginTest extends BasePlatformTestCase {

    public void testGeneratesComposedInterfaceOnDemand() throws Exception {
        myFixture.addFileToProject("src/main/java/com/tjxjnoobie/api/dependency/annotations/ComposesToInterface.java", """
                package com.tjxjnoobie.api.dependency.annotations;
                public @interface ComposesToInterface {
                    Class[] value();
                }
                """);
        myFixture.addFileToProject("src/main/java/example/domain/IDataDomain.java", """
                package example.domain;
                public interface IDataDomain extends IDataDomainGenerated {
                }
                """);
        myFixture.addFileToProject("src/main/java/example/dep/IRedis.java", """
                package example.dep;
                import com.tjxjnoobie.api.dependency.annotations.ComposesToInterface;
                import example.domain.IDataDomain;
                @ComposesToInterface(IDataDomain.class)
                public interface IRedis {
                    default void connectToRedis() {}
                }
                """);

        NovusComposeProjectService service = getProject().getService(NovusComposeProjectService.class);
        service.regenerateNow();
        service.awaitIdle();

        Path generatedFile = generatedFile("example", "domain", "IDataDomainGenerated.java");
        assertTrue(Files.exists(generatedFile));
        String contents = Files.readString(generatedFile);
        assertTrue(contents.contains("default example.dep.IRedis getRedis()"));
        assertTrue(contents.contains("DependencyLoaderAccess.findInstance(example.dep.IRedis.class)"));
        assertTrue(contents.contains("default void connectToRedis()"));
        assertFalse(contents.contains("requireDependency("));
    }

    public void testRemovesGeneratedFileWhenAnnotationIsRemoved() throws Exception {
        myFixture.addFileToProject("src/main/java/com/tjxjnoobie/api/dependency/annotations/ComposesToInterface.java", """
                package com.tjxjnoobie.api.dependency.annotations;
                public @interface ComposesToInterface {
                    Class[] value();
                }
                """);
        myFixture.addFileToProject("src/main/java/example/domain/IDataDomain.java", """
                package example.domain;
                public interface IDataDomain extends IDataDomainGenerated {
                }
                """);
        var file = myFixture.addFileToProject("src/main/java/example/dep/IRedis.java", """
                package example.dep;
                import com.tjxjnoobie.api.dependency.annotations.ComposesToInterface;
                import example.domain.IDataDomain;
                @ComposesToInterface(IDataDomain.class)
                public interface IRedis {
                    default void connectToRedis() {}
                }
                """);

        NovusComposeProjectService service = getProject().getService(NovusComposeProjectService.class);
        service.regenerateNow();
        service.awaitIdle();

        Path generatedFile = generatedFile("example", "domain", "IDataDomainGenerated.java");
        assertTrue(Files.exists(generatedFile));

        WriteAction.run(() -> {
            var document = FileDocumentManager.getInstance().getDocument(file.getVirtualFile());
            assertNotNull(document);
            document.setText("""
                    package example.dep;
                    public interface IRedis {
                        default void connectToRedis() {}
                    }
                    """);
            PsiDocumentManager.getInstance(getProject()).commitDocument(document);
        });

        service.regenerateNow();
        service.awaitIdle();
        assertFalse(Files.exists(generatedFile));
    }

    public void testInspectionShowsDuplicateGeneratedMethodNames() {
        myFixture.addFileToProject("src/main/java/com/tjxjnoobie/api/dependency/annotations/ComposesToInterface.java", """
                package com.tjxjnoobie.api.dependency.annotations;
                public @interface ComposesToInterface {
                    Class[] value();
                }
                """);
        myFixture.addFileToProject("src/main/java/example/domain/ISharedDomain.java", """
                package example.domain;
                public interface ISharedDomain {
                }
                """);
        myFixture.addFileToProject("src/main/java/example/dep/ISecond.java", """
                package example.dep;
                import com.tjxjnoobie.api.dependency.annotations.ComposesToInterface;
                import example.domain.ISharedDomain;
                @ComposesToInterface({ISharedDomain.class})
                public interface ISecond {
                    default void shared() {}
                }
                """);
        myFixture.enableInspections(new ComposesToInterfaceInspection());
        var firstFile = myFixture.addFileToProject("src/main/java/example/dep/IFirst.java", """
                package example.dep;
                import com.tjxjnoobie.api.dependency.annotations.ComposesToInterface;
                import example.domain.ISharedDomain;
                @ComposesToInterface({ISharedDomain.class})
                public interface <weak_warning descr="Multiple generated methods named 'shared' compose into example.domain.ISharedDomain: example.dep.IFirst#shared(); example.dep.ISecond#shared()">IFirst</weak_warning> {
                    default void shared() {}
                }
                """);
        getProject().getService(NovusComposeProjectService.class).regenerateNow();
        getProject().getService(NovusComposeProjectService.class).awaitIdle();
        myFixture.configureFromExistingVirtualFile(firstFile.getVirtualFile());
        myFixture.checkHighlighting();
    }

    public void testAnalyzerDetectsComposedCycles() {
        myFixture.addFileToProject("src/main/java/com/tjxjnoobie/api/dependency/annotations/ComposesToInterface.java", """
                package com.tjxjnoobie.api.dependency.annotations;
                public @interface ComposesToInterface {
                    Class[] value();
                }
                """);
        var firstFile = myFixture.addFileToProject("src/main/java/example/dep/IFirst.java", """
                package example.dep;
                import com.tjxjnoobie.api.dependency.annotations.ComposesToInterface;
                @ComposesToInterface({example.dep.ISecond.class})
                public interface IFirst {
                }
                """);
        myFixture.addFileToProject("src/main/java/example/dep/ISecond.java", """
                package example.dep;
                import com.tjxjnoobie.api.dependency.annotations.ComposesToInterface;
                @ComposesToInterface({example.dep.IFirst.class})
                public interface ISecond {
                }
                """);

        myFixture.configureFromExistingVirtualFile(firstFile.getVirtualFile());
        myFixture.doHighlighting();
        var state = CompositionProjectAnalyzer.analyze(getProject());
        var firstDiagnostics = state.diagnosticsBySource().get("example.dep.IFirst");
        var secondDiagnostics = state.diagnosticsBySource().get("example.dep.ISecond");

        assertNotNull(state.diagnosticsBySource().toString(), firstDiagnostics);
        assertNotNull(state.diagnosticsBySource().toString(), secondDiagnostics);
        assertTrue(firstDiagnostics.stream().anyMatch(diagnostic ->
                diagnostic.severity().equals(HighlightSeverity.ERROR)
                        && diagnostic.message().contains("Detected composed interface cycle")));
        assertTrue(secondDiagnostics.stream().anyMatch(diagnostic ->
                diagnostic.severity().equals(HighlightSeverity.ERROR)
                        && diagnostic.message().contains("Detected composed interface cycle")));
    }

    public void testPluginDescriptorAllowsDynamicLoadWithoutRestart() throws Exception {
        Path pluginRoot = Files.createTempDirectory("novus-compose-plugin");
        Path metaInf = Files.createDirectories(pluginRoot.resolve("META-INF"));
        Files.writeString(metaInf.resolve("plugin.xml"), """
                <idea-plugin require-restart="false">
                    <id>com.tjxjnoobie.novus.compose.intellij.test</id>
                    <name>Novus Compose IntelliJ</name>
                    <vendor>TJ</vendor>
                    <description>Instant composed interface regeneration and diagnostics for Novus DI domains.</description>
                    <depends>com.intellij.modules.platform</depends>
                    <depends>com.intellij.modules.java</depends>
                    <extensions defaultExtensionNs="com.intellij">
                        <postStartupActivity implementation="com.tjxjnoobie.tools.novuscompose.service.NovusComposeStartupActivity"/>
                        <toolWindow id="Novus Compose"
                                    anchor="right"
                                    icon="/icons/novusComposeToolWindow.svg"
                                    factoryClass="com.tjxjnoobie.tools.novuscompose.toolwindow.NovusComposeToolWindowFactory"/>
                        <localInspection language="JAVA"
                                         shortName="ComposesToInterfaceInspection"
                                         displayName="ComposesToInterface diagnostics"
                                         groupName="Novus DI"
                                         enabledByDefault="true"
                                         implementationClass="com.tjxjnoobie.tools.novuscompose.inspection.ComposesToInterfaceInspection"/>
                    </extensions>
                </idea-plugin>
                """);

        IdeaPluginDescriptorImpl descriptor = (IdeaPluginDescriptorImpl) DynamicPluginTestUtilsKt.loadDescriptorInTest(pluginRoot);
        assertTrue(DynamicPlugins.allowLoadUnloadWithoutRestart(descriptor));
    }

    public void testToolWindowTreeShowsDomainsDependenciesAndMethods() {
        seedSingleCompositionProject();

        NovusComposeProjectService service = getProject().getService(NovusComposeProjectService.class);
        service.regenerateNow();
        service.awaitIdle();

        DefaultMutableTreeNode root = NovusComposeTreeModelBuilder.buildRoot(service.getLastState());
        List<String> lines = NovusComposeTreeModelBuilder.flatten(root);

        assertContainsElements(lines,
                "  Generated Domains (1)",
                "    example.domain.IDataDomain -> example.domain.IDataDomainGenerated",
                "      Source Interfaces (1)",
                "        example.dep.IRedis via getRedis()",
                "      Generated Methods (1)",
                "        connectToRedis() -> void [example.dep.IRedis]",
                "  Dependency Interfaces (1)",
                "    example.dep.IRedis via getRedis()",
                "      Composes To (1)",
                "        example.domain.IDataDomain");
    }

    public void testToolWindowPanelSnapshotsLatestState() {
        seedSingleCompositionProject();

        NovusComposeProjectService service = getProject().getService(NovusComposeProjectService.class);
        service.regenerateNow();
        service.awaitIdle();

        NovusComposeToolWindowPanel panel = new NovusComposeToolWindowPanel(getProject());
        try {
            List<String> lines = panel.snapshotLines();
            assertContainsElements(lines,
                    "  Generated Domains (1)",
                    "    example.domain.IDataDomain -> example.domain.IDataDomainGenerated",
                    "  Dependency Interfaces (1)");
        } finally {
            Disposer.dispose(panel);
        }
    }

    public void testNavigationSupportResolvesTypeAndMethodNodes() {
        seedSingleCompositionProject();

        NovusComposeProjectService service = getProject().getService(NovusComposeProjectService.class);
        service.regenerateNow();
        service.awaitIdle();

        DefaultMutableTreeNode root = NovusComposeTreeModelBuilder.buildRoot(service.getLastState());
        DefaultMutableTreeNode sourceNode = findNode(root, "example.dep.IRedis via getRedis()");
        DefaultMutableTreeNode methodNode = findNode(root, "connectToRedis() -> void [example.dep.IRedis]");

        assertNotNull(sourceNode);
        assertNotNull(methodNode);
        assertInstanceOf(sourceNode.getUserObject(), NovusComposeTreeModelBuilder.SourceInterfaceNode.class);
        assertInstanceOf(methodNode.getUserObject(), NovusComposeTreeModelBuilder.MethodTreeNode.class);

        var sourcePsi = NovusComposeNavigationSupport.findClass(getProject(), "example.dep.IRedis");
        var methodPsi = NovusComposeNavigationSupport.findMethod(
                getProject(),
                (NovusComposeTreeModelBuilder.MethodTreeNode) methodNode.getUserObject());

        assertNotNull(sourcePsi);
        assertEquals("IRedis", sourcePsi.getName());
        assertNotNull(methodPsi);
        assertEquals("connectToRedis", methodPsi.getName());
    }

    public void testTreeRendererProvidesIconsForTypedNodes() {
        seedSingleCompositionProject();

        NovusComposeProjectService service = getProject().getService(NovusComposeProjectService.class);
        service.regenerateNow();
        service.awaitIdle();

        DefaultMutableTreeNode root = NovusComposeTreeModelBuilder.buildRoot(service.getLastState());
        DefaultMutableTreeNode domainNode = findNode(root, "example.domain.IDataDomain -> example.domain.IDataDomainGenerated");
        DefaultMutableTreeNode methodNode = findNode(root, "connectToRedis() -> void [example.dep.IRedis]");

        assertNotNull(domainNode);
        assertNotNull(methodNode);

        DefaultTreeCellRenderer renderer = new NovusComposeTreeCellRenderer();
        var domainComponent = renderer.getTreeCellRendererComponent(new JTree(root), domainNode, false, true, false, 0, false);
        var methodComponent = renderer.getTreeCellRendererComponent(new JTree(root), methodNode, false, false, true, 0, false);

        assertInstanceOf(domainComponent, DefaultTreeCellRenderer.class);
        assertInstanceOf(methodComponent, DefaultTreeCellRenderer.class);
        assertNotNull(((DefaultTreeCellRenderer) domainComponent).getIcon());
        assertNotNull(((DefaultTreeCellRenderer) methodComponent).getIcon());
    }

    public void testDuplicateWarningAutoFixAddsMethodPrefixAndRegenerates() throws Exception {
        myFixture.addFileToProject("src/main/java/com/tjxjnoobie/api/dependency/annotations/ComposesToInterface.java", """
                package com.tjxjnoobie.api.dependency.annotations;
                public @interface ComposesToInterface {
                    Class[] value();
                    String methodPrefix() default "";
                }
                """);
        myFixture.addFileToProject("src/main/java/example/domain/ISharedDomain.java", """
                package example.domain;
                public interface ISharedDomain extends ISharedDomainGenerated {
                }
                """);
        var firstFile = myFixture.addFileToProject("src/main/java/example/dep/IFirst.java", """
                package example.dep;
                import com.tjxjnoobie.api.dependency.annotations.ComposesToInterface;
                import example.domain.ISharedDomain;
                @ComposesToInterface(ISharedDomain.class)
                public interface IFirst {
                    default void shared() {}
                }
                """);
        myFixture.addFileToProject("src/main/java/example/dep/ISecond.java", """
                package example.dep;
                import com.tjxjnoobie.api.dependency.annotations.ComposesToInterface;
                import example.domain.ISharedDomain;
                @ComposesToInterface(ISharedDomain.class)
                public interface ISecond {
                    default void shared() {}
                }
                """);

        NovusComposeProjectService service = getProject().getService(NovusComposeProjectService.class);
        service.regenerateNow();
        service.awaitIdle();

        DefaultMutableTreeNode root = NovusComposeTreeModelBuilder.buildRoot(service.getLastState());
        DefaultMutableTreeNode warningNode = findNodeContaining(
                root,
                "Multiple generated methods named 'shared' compose into example.domain.ISharedDomain");
        assertNotNull(warningNode);

        assertTrue(NovusComposeQuickFixes.isFixableWarning(warningNode.getUserObject()));
        assertTrue(NovusComposeQuickFixes.applyFix(getProject(), warningNode.getUserObject()));

        String sourceContents = firstFile.getText();
        assertTrue(sourceContents.contains("methodPrefix = \"first\""));

        service.regenerateNow();
        service.awaitIdle();
        String generatedContents = Files.readString(generatedFile("example", "domain", "ISharedDomainGenerated.java"));
        assertTrue(generatedContents.contains("default void firstShared()"));
    }

    public void testPluginDescriptorRegistersToolWindowExtension() throws Exception {
        String pluginXml = Files.readString(Path.of(
                "F:/workspace/MCRSpeedrun/NovusComposeIntellij/src/main/resources/META-INF/plugin.xml"));
        assertTrue(pluginXml.contains("<toolWindow id=\"" + NovusComposeToolWindowFactory.TOOL_WINDOW_ID + "\""));
        assertTrue(pluginXml.contains("factoryClass=\"com.tjxjnoobie.tools.novuscompose.toolwindow.NovusComposeToolWindowFactory\""));
        assertTrue(pluginXml.contains("icon=\"/icons/novusComposeToolWindow.svg\""));
    }

    private Path generatedFile(String... segments) {
        String contentRootPath = ModuleRootManager.getInstance(getModule()).getContentRoots()[0].getPath();
        Path generatedRoot = Path.of(contentRootPath, ".novus-generated");
        Path relativePath = Path.of("", segments);
        if (Files.exists(generatedRoot)) {
            try (var stream = Files.walk(generatedRoot)) {
                Path match = stream
                        .filter(Files::isRegularFile)
                        .filter(path -> path.endsWith(relativePath))
                        .findFirst()
                        .orElse(null);
                if (match != null) {
                    return match;
                }
            } catch (Exception ignored) {
            }
        }
        Path base = generatedRoot.resolve(getModule().getName()).resolve(Path.of("src", "main", "java"));
        for (String segment : segments) {
            base = base.resolve(segment);
        }
        return base;
    }

    private void seedSingleCompositionProject() {
        myFixture.addFileToProject("src/main/java/com/tjxjnoobie/api/dependency/annotations/ComposesToInterface.java", """
                package com.tjxjnoobie.api.dependency.annotations;
                public @interface ComposesToInterface {
                    Class[] value();
                }
                """);
        myFixture.addFileToProject("src/main/java/example/domain/IDataDomain.java", """
                package example.domain;
                public interface IDataDomain extends IDataDomainGenerated {
                }
                """);
        myFixture.addFileToProject("src/main/java/example/dep/IRedis.java", """
                package example.dep;
                import com.tjxjnoobie.api.dependency.annotations.ComposesToInterface;
                import example.domain.IDataDomain;
                @ComposesToInterface(IDataDomain.class)
                public interface IRedis {
                    default void connectToRedis() {}
                }
                """);
    }

    private DefaultMutableTreeNode findNode(DefaultMutableTreeNode root, String label) {
        String currentLabel = root.getUserObject() instanceof NovusComposeTreeModelBuilder.TreeNodeValue treeNodeValue
                ? treeNodeValue.label()
                : root.getUserObject().toString();
        if (label.equals(currentLabel)) {
            return root;
        }
        for (int index = 0; index < root.getChildCount(); index++) {
            DefaultMutableTreeNode match = findNode((DefaultMutableTreeNode) root.getChildAt(index), label);
            if (match != null) {
                return match;
            }
        }
        return null;
    }

    private DefaultMutableTreeNode findNodeContaining(DefaultMutableTreeNode root, String fragment) {
        String currentLabel = root.getUserObject() instanceof NovusComposeTreeModelBuilder.TreeNodeValue treeNodeValue
                ? treeNodeValue.label()
                : root.getUserObject().toString();
        if (currentLabel.contains(fragment)) {
            return root;
        }
        for (int index = 0; index < root.getChildCount(); index++) {
            DefaultMutableTreeNode match = findNodeContaining((DefaultMutableTreeNode) root.getChildAt(index), fragment);
            if (match != null) {
                return match;
            }
        }
        return null;
    }
}
