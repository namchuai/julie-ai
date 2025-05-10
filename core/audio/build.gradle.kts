plugins {
    alias(libs.plugins.julie.kotlinMultiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.logging)
            implementation(libs.kotlinx.coroutines.core)
        }

        desktopMain.dependencies {
            // For macOS audio recording
        }

        androidMain.dependencies {
            // For Android audio recording
        }
    }
}