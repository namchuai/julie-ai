plugins {
    alias(libs.plugins.julie.kotlinMultiplatform)
    alias(libs.plugins.sqlDelight)
}

repositories {
    google()
    mavenCentral()
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("ui.julie.storage")
        }
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(libs.kotbase)
        }

        val desktopMain by getting
        desktopMain.dependencies {
            implementation(libs.sqldelight.jvm.driver)
        }

        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.sqldelight.android.driver)
        }

        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
    }
}