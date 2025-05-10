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

            implementation(projects.feature.modelconfig.impl)
            implementation(projects.core.logging)
            implementation(projects.core.model)
            implementation(projects.core.llamabinding)
            implementation(projects.feature.modelmanagement.api)
            implementation(projects.feature.modelmanagement.impl)
            implementation(projects.feature.thread.api)
            implementation(projects.feature.thread.impl)
            implementation(projects.feature.message.api)
            implementation(projects.feature.message.impl)
            implementation(projects.feature.modelconfig.api)
            implementation(projects.feature.modelconfig.impl)
            implementation(projects.feature.toolexecutor.api)
            implementation(projects.feature.toolexecutor.impl)

            implementation(libs.navigation.compose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.koin.composeVM)
            implementation(libs.markdown.renderer)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}
