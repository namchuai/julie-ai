plugins {
    alias(libs.plugins.julie.kotlinMultiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(projects.core.fs)
            implementation(libs.kotlinx.datetime)
            implementation(projects.core.network)
            implementation(projects.core.logging)
            api(projects.core.data)
            api(libs.okio)
        }
    }
}
