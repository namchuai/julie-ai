plugins {
    alias(libs.plugins.julie.kotlinMultiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.logging)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}