enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven("https://packages.jetbrains.team/maven/p/firework/dev")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

include(
    ":composeApp",
    ":core:common",
    ":core:domain",
    ":core:model",
    ":core:network",
    ":core:designsystem",
    ":core:data",
    ":core:logging",
    ":core:storage",
    ":core:llamabinding",
    ":core:llamabinding:llamacpp",
    ":feature:chat",
    ":feature:modelmanagement",
    ":feature:modelmarket",
    ":feature:modelconfig",
    ":feature:thread",
    ":core:fs",
)
rootProject.name = "Julie"

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven("https://packages.jetbrains.team/maven/p/firework/dev")
    }
}
