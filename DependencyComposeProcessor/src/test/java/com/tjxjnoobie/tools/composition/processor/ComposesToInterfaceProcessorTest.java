package com.tjxjnoobie.tools.composition.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import static com.google.testing.compile.CompilationSubject.assertThat;

class ComposesToInterfaceProcessorTest {

    @Test
    void generatesComposedDomainBridge() {
        Compilation compilation = compile(
                JavaFileObjects.forSourceString(
                        "com.tjxjnoobie.api.dependency.annotations.ComposesToInterface",
                        """
                        package com.tjxjnoobie.api.dependency.annotations;
                        public @interface ComposesToInterface {
                            Class<?>[] value();
                        }
                        """),
                JavaFileObjects.forSourceString(
                        "com.example.domain.IDataBaseDomain",
                        """
                        package com.example.domain;
                        public interface IDataBaseDomain extends IDataBaseDomainGenerated {
                        }
                        """),
                JavaFileObjects.forSourceString(
                        "com.example.dep.IRedis",
                        """
                        package com.example.dep;
                        import com.tjxjnoobie.api.dependency.annotations.ComposesToInterface;
                        import com.example.domain.IDataBaseDomain;
                        @ComposesToInterface(IDataBaseDomain.class)
                        public interface IRedis {
                            default void connectToRedis() {}
                        }
                        """),
                JavaFileObjects.forSourceString(
                        "com.example.dep.IUtils",
                        """
                        package com.example.dep;
                        import com.tjxjnoobie.api.dependency.annotations.ComposesToInterface;
                        import com.example.domain.IDataBaseDomain;
                        @ComposesToInterface(IDataBaseDomain.class)
                        public interface IUtils {
                            default String createServerID() { return "ok"; }
                        }
                        """));

        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("com.example.domain.IDataBaseDomainGenerated")
                .contentsAsUtf8String()
                .contains("public interface IDataBaseDomainGenerated");
        assertThat(compilation)
                .generatedSourceFile("com.example.domain.IDataBaseDomainGenerated")
                .contentsAsUtf8String()
                .contains("default com.example.dep.IRedis redisDependency()");
        assertThat(compilation)
                .generatedSourceFile("com.example.domain.IDataBaseDomainGenerated")
                .contentsAsUtf8String()
                .contains("default com.example.dep.IUtils utilsDependency()");
        assertThat(compilation)
                .generatedSourceFile("com.example.domain.IDataBaseDomainGenerated")
                .contentsAsUtf8String()
                .contains("default void connectToRedis()");
        assertThat(compilation)
                .generatedSourceFile("com.example.domain.IDataBaseDomainGenerated")
                .contentsAsUtf8String()
                .contains("default java.lang.String createServerID()");
    }

    @Test
    void warnsAndSkipsExactSignatureCollisions() {
        Compilation compilation = compile(
                annotationSource(),
                JavaFileObjects.forSourceString(
                        "com.example.domain.ISharedDomain",
                        """
                        package com.example.domain;
                        public interface ISharedDomain extends ISharedDomainGenerated {
                        }
                        """),
                JavaFileObjects.forSourceString(
                        "com.example.dep.IFirst",
                        """
                        package com.example.dep;
                        import com.tjxjnoobie.api.dependency.annotations.ComposesToInterface;
                        import com.example.domain.ISharedDomain;
                        @ComposesToInterface(ISharedDomain.class)
                        public interface IFirst {
                            default void shared() {}
                        }
                        """),
                JavaFileObjects.forSourceString(
                        "com.example.dep.ISecond",
                        """
                        package com.example.dep;
                        import com.tjxjnoobie.api.dependency.annotations.ComposesToInterface;
                        import com.example.domain.ISharedDomain;
                        @ComposesToInterface(ISharedDomain.class)
                        public interface ISecond {
                            default void shared() {}
                        }
                        """));

        assertThat(compilation).succeeded();
        assertThat(compilation).hadWarningContaining("Skipping generated forwarding method 'shared()'");
        assertThat(compilation)
                .generatedSourceFile("com.example.domain.ISharedDomainGenerated")
                .contentsAsUtf8String()
                .contains("Multiple generated methods share the name 'shared'");
    }

    @Test
    void failsOnCompositionCycles() {
        Compilation compilation = compile(
                annotationSource(),
                JavaFileObjects.forSourceString(
                        "com.example.dep.IFirst",
                        """
                        package com.example.dep;
                        import com.tjxjnoobie.api.dependency.annotations.ComposesToInterface;
                        @ComposesToInterface(ISecond.class)
                        public interface IFirst {
                        }
                        """),
                JavaFileObjects.forSourceString(
                        "com.example.dep.ISecond",
                        """
                        package com.example.dep;
                        import com.tjxjnoobie.api.dependency.annotations.ComposesToInterface;
                        @ComposesToInterface(IFirst.class)
                        public interface ISecond {
                        }
                        """));

        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("Detected composed interface cycle");
    }

    private Compilation compile(javax.tools.JavaFileObject... sources) {
        return com.google.testing.compile.Compiler.javac()
                .withProcessors(new ComposesToInterfaceProcessor())
                .compile(sources);
    }

    private javax.tools.JavaFileObject annotationSource() {
        return JavaFileObjects.forSourceString(
                "com.tjxjnoobie.api.dependency.annotations.ComposesToInterface",
                """
                package com.tjxjnoobie.api.dependency.annotations;
                public @interface ComposesToInterface {
                    Class<?>[] value();
                }
                """);
    }
}
