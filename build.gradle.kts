import java.util.zip.ZipFile

plugins {
    base
}

group = "com.tjxjnoobie"
version = "0"

val springBootBom = "org.springframework.boot:spring-boot-dependencies:4.1.0"
val testcontainersVersion = "2.0.5"
val junit = "org.junit.jupiter:junit-jupiter:5.10.2"
val junitLauncher = "org.junit.platform:junit-platform-launcher:1.10.2"
val mockito = "org.mockito:mockito-core:5.12.0"

subprojects {
    group = rootProject.group
    version = rootProject.version

    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion = JavaLanguageVersion.of(25)
        withSourcesJar()
        withJavadocJar()
    }

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.dmulloy2.net/repository/public/")
        maven("https://repo.codemc.io/repository/maven-public/")
    }

    dependencyLocking {
        lockAllConfigurations()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.add("-parameters")
        options.encoding = "UTF-8"
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        maxHeapSize = "512m"
        jvmArgs("-XX:MaxMetaspaceSize=384m")
    }

    tasks.withType<Jar>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    val verifyJarContents = tasks.register("verifyJarContents") {
        dependsOn(tasks.named("jar"))
        val archive = tasks.named<Jar>("jar").flatMap { it.archiveFile }
        inputs.file(archive)
        doLast {
            val forbidden = listOf(
                "com/fasterxml/",
                "com/mysql/",
                "com/stripe/",
                "com/velocitypowered/",
                "io/papermc/",
                "jakarta/",
                "org/flywaydb/",
                "org/postgresql/",
                "org/springframework/",
                "org/thymeleaf/",
                "org/yaml/",
                "redis/clients/",
            )
            ZipFile(archive.get().asFile).use { jar ->
                val embedded = jar.entries().asSequence().map { it.name }
                    .firstOrNull { entry -> forbidden.any(entry::startsWith) }
                check(embedded == null) { "Third-party class embedded in first-party JAR: $embedded" }
            }
        }
    }

    tasks.named("check") {
        dependsOn(verifyJarContents)
    }

    extensions.configure<PublishingExtension> {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                artifactId = project.name
            }
        }
        repositories {
            val token = providers.environmentVariable("GITHUB_TOKEN")
            if (token.isPresent) {
                maven {
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/TavallStudios/MCRSpeedrun")
                    credentials {
                        username = providers.environmentVariable("GITHUB_ACTOR").orNull
                        password = token.get()
                    }
                }
            }
        }
    }
}

project(":api") {
    dependencies {
        "api"("org.tavall:tavall-di:1.0.0")
        "annotationProcessor"("org.tavall:tavall-di:1.0.0")
        "api"("org.tavall:tavall-eventbus:1.0.0")
        "api"("org.tavall:tavall-logging:1.0.0")
        "api"("org.tavall:tavall-concurrency:1.0.0")
        "api"("org.tavall:tavall-reflection:1.0.0")
        "api"("org.tavall:tavall-scheduler:1.0.0")
        "compileOnlyApi"("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
        "compileOnlyApi"("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
        "compileOnly"("com.mysql:mysql-connector-j:9.3.0")
        "compileOnly"("redis.clients:jedis:5.2.0")
        "testRuntimeOnly"("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
        "testRuntimeOnly"("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
        "testRuntimeOnly"("com.mysql:mysql-connector-j:9.3.0")
        "testRuntimeOnly"("redis.clients:jedis:5.2.0")
        "testImplementation"(junit)
        "testImplementation"(mockito)
        "testRuntimeOnly"(junitLauncher)
    }
}

project(":core") {
    dependencies {
        "api"(project(":api"))
        "compileOnlyApi"("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    }
}

project(":minecraft-integration") {
    dependencies {
        "api"(platform(springBootBom))
        "api"("com.fasterxml.jackson.core:jackson-databind")
    }
}

project(":store-domain") {
    dependencies {
        "api"(platform(springBootBom))
        "api"("org.springframework:spring-context")
        "api"("org.springframework:spring-core")
        "api"("org.springframework:spring-web")
        "api"("jakarta.validation:jakarta.validation-api")
        "api"("com.fasterxml.jackson.core:jackson-annotations")
        "testImplementation"(junit)
        "testRuntimeOnly"(junitLauncher)
    }
}

project(":store-persistence") {
    dependencies {
        "implementation"(platform(springBootBom))
        "testImplementation"(platform(springBootBom))
        "api"(project(":store-domain"))
        "api"("org.springframework.boot:spring-boot-starter-data-jpa")
        "implementation"("org.springframework.boot:spring-boot-starter-flyway")
        "runtimeOnly"("org.flywaydb:flyway-database-postgresql")
        "runtimeOnly"("org.postgresql:postgresql")
        "implementation"("com.fasterxml.jackson.core:jackson-databind")
        "testImplementation"("org.springframework.boot:spring-boot-starter-test")
        "testImplementation"("org.testcontainers:testcontainers-junit-jupiter:$testcontainersVersion")
        "testImplementation"("org.testcontainers:testcontainers-postgresql:$testcontainersVersion")
        "testRuntimeOnly"(junitLauncher)
    }
}

project(":velocitycore") {
    dependencies {
        "compileOnlyApi"("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
        "testRuntimeOnly"("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
        "implementation"("org.yaml:snakeyaml:2.2")
        "runtimeOnly"("com.mysql:mysql-connector-j:9.3.0")
        "api"(project(":api"))
        "implementation"("com.tjxjnoobie:tavall-rating-glicko2:1.0.0")
        "implementation"(project(":minecraft-integration"))
        "runtimeOnly"("redis.clients:jedis:5.2.0")
        "testImplementation"(junit)
        "testImplementation"(mockito)
        "testImplementation"("org.testcontainers:testcontainers-junit-jupiter:$testcontainersVersion")
        "testImplementation"("org.testcontainers:testcontainers-mysql:$testcontainersVersion")
        "testRuntimeOnly"(junitLauncher)
    }
}

project(":kingdomfactions") {
    group = "com.tjxjnoobie.kingdomfactions"
    dependencies {
        "compileOnlyApi"("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
        "testRuntimeOnly"("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
        "api"(project(":api"))
    }
}

project(":speedrun") {
    dependencies {
        "compileOnlyApi"("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
        "compileOnly"("net.dmulloy2:ProtocolLib:5.4.0")
        "testRuntimeOnly"("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
        "testRuntimeOnly"("net.dmulloy2:ProtocolLib:5.4.0")
        "api"(project(":api"))
        "implementation"("org.tavall:tavall-reflection:1.0.0")
    }
}

project(":website") {
    apply(plugin = "application")
    extensions.configure<JavaApplication> {
        mainClass = "com.tjxjnoobie.website.WebsiteApplication"
    }
    tasks.named<Jar>("jar") {
        manifest {
            attributes["Main-Class"] = "com.tjxjnoobie.website.WebsiteApplication"
        }
    }
    dependencies {
        "implementation"(platform(springBootBom))
        "testImplementation"(platform(springBootBom))
        "implementation"(project(":minecraft-integration"))
        "implementation"(project(":store-domain"))
        "implementation"(project(":store-persistence"))
        "implementation"("org.springframework.boot:spring-boot-starter-web")
        "implementation"("org.springframework.boot:spring-boot-starter-thymeleaf")
        "implementation"("org.springframework.boot:spring-boot-starter-security")
        "implementation"("org.springframework.boot:spring-boot-starter-oauth2-client")
        "implementation"("org.springframework.boot:spring-boot-starter-validation")
        "implementation"("org.springframework.boot:spring-boot-starter-actuator")
        "implementation"("org.springframework.boot:spring-boot-jackson2")
        "implementation"("org.springframework.boot:spring-boot-starter-flyway")
        "runtimeOnly"("org.flywaydb:flyway-database-postgresql")
        "runtimeOnly"("org.postgresql:postgresql")
        "implementation"("com.stripe:stripe-java:32.0.0")
        "implementation"("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
        "runtimeOnly"("org.springframework.boot:spring-boot-devtools")
        "implementation"("org.springframework.security:spring-security-oauth2-client")
        "testImplementation"("org.springframework.boot:spring-boot-starter-test")
        "testImplementation"("org.springframework.boot:spring-boot-webmvc-test")
        "testImplementation"("io.projectreactor:reactor-test")
        "testImplementation"("org.springframework.security:spring-security-test")
        "testImplementation"("org.testcontainers:testcontainers-junit-jupiter:$testcontainersVersion")
        "testImplementation"("org.testcontainers:testcontainers-postgresql:$testcontainersVersion")
        "testRuntimeOnly"(junitLauncher)
    }
}

fun Project.registerExternalIntegrationTest(vararg classPatterns: String) {
    val testSourceSet = extensions.getByType<SourceSetContainer>().named("test")
    tasks.named<Test>("test") {
        classPatterns.forEach { exclude(it) }
        failOnNoDiscoveredTests = false
    }
    tasks.register<Test>("integrationTest") {
        description = "Runs tests that require Docker or an external database."
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        testClassesDirs = testSourceSet.get().output.classesDirs
        classpath = testSourceSet.get().runtimeClasspath
        classPatterns.forEach { include(it) }
        shouldRunAfter(tasks.named("test"))
    }
}

project(":store-persistence").registerExternalIntegrationTest(
    "**/*Test.class",
    "**/*Tests.class",
)
project(":website").registerExternalIntegrationTest(
    "**/*Test.class",
    "**/*ApplicationTests.class",
)
project(":velocitycore").registerExternalIntegrationTest(
    "**/MockVelocityLifecycleTest.class",
)

val stageDistribution = tasks.register<Sync>("stageDistribution") {
    dependsOn(":speedrun:jar", ":velocitycore:jar", ":website:jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    into(layout.projectDirectory.dir("distribution"))

    from(project(":speedrun").tasks.named<Jar>("jar").flatMap { it.archiveFile }) {
        into("plugins")
        rename { "speedrun.jar" }
    }
    from(project(":velocitycore").tasks.named<Jar>("jar").flatMap { it.archiveFile }) {
        into("plugins")
        rename { "velocitycore.jar" }
    }
    from(project(":speedrun").configurations.named("runtimeClasspath")) {
        into("plugins/libs")
    }
    from(project(":velocitycore").configurations.named("runtimeClasspath")) {
        into("plugins/libs")
    }
    from(project(":website").tasks.named<Jar>("jar").flatMap { it.archiveFile }) {
        into("website")
        rename { "application.jar" }
    }
    from(project(":website").configurations.named("runtimeClasspath")) {
        into("website/libs")
    }
}

tasks.named("assemble") {
    dependsOn(stageDistribution)
}
