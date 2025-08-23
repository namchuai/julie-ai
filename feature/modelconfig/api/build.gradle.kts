plugins {
    alias(libs.plugins.julie.kotlinMultiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(projects.core.logging)
            implementation(projects.feature.message.api)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.filekit.core)
            implementation(libs.kotbase)
        }
    }
}