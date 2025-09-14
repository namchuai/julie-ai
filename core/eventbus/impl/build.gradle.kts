plugins {
    alias(libs.plugins.julie.kotlinMultiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.eventbus.api)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}