plugins {
    alias(libs.plugins.julie.kotlinMultiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.buildConfig)
}

kotlin {
    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonMain.dependencies {
            implementation(projects.core.model)

            implementation(libs.kotlinx.serialization.json)
            api(libs.bundles.ktor.common)
        }
        desktopMain.dependencies {
            implementation(libs.ktor.client.java)
        }
    }
}

