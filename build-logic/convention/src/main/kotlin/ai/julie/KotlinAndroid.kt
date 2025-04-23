package ai.julie

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project

internal fun Project.configureKotlinAndroid(
    extension: LibraryExtension,
) = extension.apply {
    val moduleName = path.split(":").drop(2).joinToString(".")
    namespace = if (moduleName.isNotEmpty()) "ai.julie.$moduleName" else "ai.julie"

    compileSdk = libs.findVersion("android.compileSdk").get().requiredVersion.toInt()
    defaultConfig {
        minSdk = libs.findVersion("android.minSdk").get().requiredVersion.toInt()
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildFeatures {
        buildConfig = true
    }
}