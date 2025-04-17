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
    }
}

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
    }
}

rootProject.name = "Julie"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":composeApp")
include(":core:common")
include(":core:domain")
include(":core:model")
include(":core:network")
include(":core:designsystem")
include(":core:data")
include(":core:nativelib")
include(":core:logging")

include(":feature:chat")
include(":feature:modelmanagement")
