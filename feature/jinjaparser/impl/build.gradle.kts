plugins {
    alias(libs.plugins.julie.kotlinMultiplatform)
    alias(libs.plugins.julie.composeMultiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.jinjaparser.api)
            implementation(projects.core.model)
            implementation(projects.core.logging)
            implementation(projects.feature.message.api)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
        }
        
        desktopMain.dependencies {
            implementation(libs.jinjava)
        }
    }
}