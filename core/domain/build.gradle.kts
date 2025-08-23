plugins {
    alias(libs.plugins.julie.kotlinMultiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(projects.core.permission)
            implementation(projects.core.audio)
            implementation(libs.kotlinx.datetime)
            implementation(projects.core.network)
            implementation(projects.core.logging)
            implementation(projects.core.llamabinding)
            api(projects.core.data)
            implementation(projects.feature.modelmanagement.api)
            implementation(projects.feature.jinjaparser.api)
            implementation(projects.feature.message.api)
            implementation(libs.filekit.core)
        }

        desktopMain.dependencies {
            implementation(libs.jinjava)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
