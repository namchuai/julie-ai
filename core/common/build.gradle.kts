plugins {
    alias(libs.plugins.julie.kotlinMultiplatform)
    alias(libs.plugins.julie.composeMultiplatform)
    alias(libs.plugins.kotlin.parcelize)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "ai.julie.resources"
    generateResClass = always
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(compose.components.resources)
        }
    }
}
