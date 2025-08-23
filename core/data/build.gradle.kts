plugins {
    alias(libs.plugins.julie.kotlinMultiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(projects.core.network)
            implementation(projects.core.logging)
            implementation(projects.core.permission)
            implementation(projects.core.llamabinding)
            implementation(projects.core.whisperbinding)
            implementation(libs.filekit.core)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.io.core)
        }
    }
}
