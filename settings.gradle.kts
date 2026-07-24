plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "MCRSpeedrun"

include(
    "api",
    "core",
    "minecraft-integration",
    "store-domain",
    "store-persistence",
    "velocitycore",
    "kingdomfactions",
    "speedrun",
    "website",
)

project(":api").projectDir = file("API")
project(":core").projectDir = file("Core")
project(":minecraft-integration").projectDir = file("MinecraftIntegration")
project(":store-domain").projectDir = file("StoreDomain")
project(":store-persistence").projectDir = file("StorePersistence")
project(":velocitycore").projectDir = file("VelocityCore")
project(":kingdomfactions").projectDir = file("KingdomFactions")
project(":speedrun").projectDir = file("SpeedRun")
project(":website").projectDir = file("Website")
