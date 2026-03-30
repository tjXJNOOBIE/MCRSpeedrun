package com.tjxjnoobie.tools.novuscompose;

import com.intellij.ide.plugins.DynamicPlugins;
import com.intellij.ide.plugins.IdeaPluginDescriptorImpl;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.platform.testFramework.DynamicPluginTestUtilsKt;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.tjxjnoobie.tools.novuscompose.analysis.CompositionProjectAnalyzer;
import com.tjxjnoobie.tools.novuscompose.inspection.ComposesToInterfaceInspection;
import com.tjxjnoobie.tools.novuscompose.service.NovusComposeProjectService;

import java.nio.file.Files;
import java.nio.file.Path;

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

        Path generatedFile = generatedFile("example", "domain", "IDataDomainGenerated.java");
        assertTrue(Files.exists(generatedFile));
        String contents = Files.readString(generatedFile);
        assertTrue(contents.contains("default example.dep.IRedis redisDependency()"));
        assertTrue(contents.contains("default void connectToRedis()"));
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

    private Path generatedFile(String... segments) {
        String contentRootPath = ModuleRootManager.getInstance(getModule()).getContentRoots()[0].getPath();
        Path base = Path.of(contentRootPath, ".novus-generated", getModule().getName(), "src", "main", "java");
        for (String segment : segments) {
            base = base.resolve(segment);
        }
        return base;
    }
}
