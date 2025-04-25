plugins {
    alias(libs.plugins.julie.kotlinMultiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(libs.kotlinx.datetime)
            api(projects.core.data)
            api(libs.okio)
        }
    }
}
