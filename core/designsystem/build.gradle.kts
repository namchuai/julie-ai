plugins {
    alias(libs.plugins.julie.kotlinMultiplatform)
    alias(libs.plugins.julie.composeMultiplatform)
    alias(libs.plugins.lumo)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            api(libs.nomanr.composables)
            implementation(libs.coil.compose)
            implementation(libs.zoomimage.compose.coil)
        }
    }
}
