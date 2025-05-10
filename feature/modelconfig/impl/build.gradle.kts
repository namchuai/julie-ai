plugins {
    alias(libs.plugins.julie.kotlinMultiplatform)
    alias(libs.plugins.julie.composeMultiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.modelconfig.api)
            implementation(projects.feature.thread.api)
            implementation(projects.core.model)
            implementation(projects.core.logging)
            implementation(projects.core.domain)
            implementation(projects.core.designsystem)
            implementation(projects.core.common)
            
            implementation(libs.kotbase)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.filekit.core)
            implementation(libs.navigation.compose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.koin.composeVM)
            implementation(libs.composeicons.feather)
        }

        desktopMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}