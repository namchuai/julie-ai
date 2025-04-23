plugins {
    alias(libs.plugins.julie.kotlinMultiplatform)
    alias(libs.plugins.julie.composeMultiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.designsystem)
            api(projects.core.common)
            api(projects.core.domain)

            implementation(projects.core.llamabinding)
            implementation(projects.core.logging)
            implementation(projects.core.model)
            implementation(projects.core.storage)

            implementation(libs.navigation.compose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.koin.composeVM)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(projects.core.llamabinding)
        }
    }
}
