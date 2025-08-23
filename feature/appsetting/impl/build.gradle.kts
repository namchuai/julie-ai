plugins {
    alias(libs.plugins.julie.kotlinMultiplatform)
    alias(libs.plugins.julie.composeMultiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.appsetting.api)
            implementation(projects.core.designsystem)
            implementation(projects.core.common)
            implementation(projects.core.logging)

            implementation(libs.navigation.compose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.koin.composeVM)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
            implementation(libs.kotbase)
        }

        desktopMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}