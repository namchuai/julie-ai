plugins {
    alias(libs.plugins.julie.kotlinMultiplatform)
    alias(libs.plugins.julie.composeMultiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.toolexecutor.api)
            implementation(projects.core.domain)
            implementation(projects.core.model)
            implementation(projects.core.logging)
            implementation(projects.core.data)
            implementation(projects.core.network)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.composeVM)
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(projects.core.designsystem)
            implementation(projects.core.common)
            implementation(libs.bundles.ktor.common)
            implementation(libs.kotlinx.serialization.json)
        }

        desktopMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}