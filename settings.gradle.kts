rootProject.name = "Localflix"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":androidApp")
include(":shared")

// Desktop + web are paused while we focus on Android + iOS.
// To re-enable: set localflix.allTargets=true in gradle.properties and re-sync.
// (The shared module reads the same flag to turn its jvm/js/wasmJs targets back on.)
if (providers.gradleProperty("localflix.allTargets").orNull == "true") {
    include(":desktopApp")
    include(":webApp")
}
