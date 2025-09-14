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
    ":core:permission",
    ":core:audio",
    ":core:eventbus:api",
    ":core:eventbus:impl",
    ":core:llamabinding",
    ":core:llamabinding:llamacpp",
    ":core:whisperbinding",
    ":core:whisperbinding:whispercpp",
    ":feature:chat",
    ":feature:modelmanagement:api",
    ":feature:modelmanagement:impl",
    ":feature:modelmarket",
    ":feature:modelconfig:api",
    ":feature:modelconfig:impl",
    ":feature:thread:api",
    ":feature:thread:impl",
    ":feature:message:api",
    ":feature:message:impl",
    ":feature:jinjaparser:api",
    ":feature:jinjaparser:impl",
    ":feature:promptlab:api",
    ":feature:promptlab:impl",
    ":feature:pythonrunner:api",
    ":feature:pythonrunner:impl",
    ":feature:hardwaremonitor:api",
    ":feature:hardwaremonitor:impl",
    ":feature:auth",
    ":feature:appsetting:api",
    ":feature:appsetting:impl",
    ":feature:toolexecutor:api",
    ":feature:toolexecutor:impl",
    ":feature:toolmanagement:api",
    ":feature:toolmanagement:impl",
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
