plugins {
    alias(libs.plugins.julie.kotlinMultiplatform)
    alias(libs.plugins.julie.composeMultiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.modelmanagement.api)
            implementation(projects.feature.modelconfig.api)
            implementation(projects.feature.modelconfig.impl)
            implementation(projects.feature.thread.api)
            implementation(projects.core.domain)
            implementation(projects.core.model)
            implementation(projects.core.logging)
            implementation(projects.core.data)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.composeVM)
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.nomanr.composables)
            implementation(libs.markdown.renderer)
            implementation(libs.zoomimage.compose.coil)
            implementation(projects.core.designsystem)
            implementation(projects.core.common)
            implementation(projects.core.llamabinding)
            implementation(projects.feature.auth)
            implementation(libs.filekit.core)
            implementation(libs.filekit.dialogs)
            implementation(libs.filekit.dialogs.compose)
            implementation(libs.kotbase)
        }

        desktopMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}