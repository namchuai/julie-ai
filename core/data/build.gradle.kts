plugins {
    alias(libs.plugins.julie.kotlinMultiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(projects.core.network)
            implementation(projects.core.logging)
            implementation(projects.core.storage)
            implementation(projects.core.llamabinding)

            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
