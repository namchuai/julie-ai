import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.reload.ComposeHotRun
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    androidTarget {
        compilerOptions {
            JavaVersion.VERSION_17
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.koin.android)

            implementation(libs.ktor.client.android)
            implementation(libs.kotlinx.coroutines.android)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(projects.feature.chat)
            implementation(projects.feature.modelmarket)
            implementation(projects.feature.modelmanagement)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.navigation.compose)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        val desktopMain by getting
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }

        desktopMain.resources.srcDirs("build/processedResources/desktop/main")
    }
}

android {
    namespace = "ai.julie"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "ai.julie"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    dependencies {
        debugImplementation(compose.uiTooling)
    }
}

compose {
    // specify resources root if not commonMain/resources
    // depends on the compose plugin version
    // see: https://github.com/JetBrains/compose-multiplatform/blob/master/CHANGELOG.md
    resources {
        packageOfResClass = "ai.julie.resources"
    }
}

compose.desktop {
    application {
        mainClass = "ai.julie.MainKt"

        // Define the path to the native library directory
        // val nativeLibDir = layout.buildDirectory.dir("processedResources/desktop/main").get().asFile // Old path
        // Define paths to the CMake output directories relative to composeApp build
        val llamacppProject = project(":core:llamabinding:llamacpp")
        val cmakeBuildDirLib = llamacppProject.buildDir.resolve("cmake-build/lib")
        val cmakeBuildDirBin = llamacppProject.buildDir.resolve("cmake-build/bin")

        // Set the java.library.path using jvmArgs to include both directories
        // Ensure the directories exist before trying to set the property
        if (cmakeBuildDirLib.isDirectory && cmakeBuildDirBin.isDirectory) {
            jvmArgs += "-Djava.library.path=${cmakeBuildDirLib.absolutePath}${File.pathSeparator}${cmakeBuildDirBin.absolutePath}"
        } else {
            // Optionally add a warning if the directories don't exist at configuration time
            logger.warn("CMake output directories not found during configuration: ${cmakeBuildDirLib.absolutePath} or ${cmakeBuildDirBin.absolutePath}")
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ai.julie"
            packageVersion = "1.0.0"

            // Include the native library from the nativelib module's build output
            modules("java.instrument") // Example existing module, keep others if present
            includeAllModules = true // Ensure dependent modules are included
        }
    }
}

// Ensure native lib is copied before desktop resources are processed for packaging/running
// Ensure this dependency points to the CORRECT module (:llamacpp) now
tasks.named("desktopProcessResources").configure {
    dependsOn(project(":core:llamabinding:llamacpp").tasks.named("copyHostNativeLib")) // Renamed task
}

composeCompiler {
    featureFlags.add(ComposeFeatureFlag.OptimizeNonSkippingGroups)
}

tasks.register<ComposeHotRun>("runHot") {
    mainClass.set("ai.julie.MainKt")
}

// --- Explicitly configure the 'run' task --- 
tasks.withType<JavaExec>().configureEach { // Configure ALL JavaExec tasks (includes 'run')
    // Depends on the native library being built
    dependsOn(project(":core:llamabinding:llamacpp").tasks.named("buildHostCMake")) // Renamed task

    // Set the system property directly on the task
    val llamacppProject = project(":core:llamabinding:llamacpp")
    val cmakeBuildDirLib = llamacppProject.buildDir.resolve("cmake-build/lib")
    val cmakeBuildDirBin = llamacppProject.buildDir.resolve("cmake-build/bin")

    if (cmakeBuildDirLib.isDirectory && cmakeBuildDirBin.isDirectory) {
        systemProperty(
            "java.library.path",
            "${cmakeBuildDirLib.absolutePath}${File.pathSeparator}${cmakeBuildDirBin.absolutePath}"
        )
    } else {
        logger.warn("CMake output directories not found when configuring run task: ${cmakeBuildDirLib.absolutePath} or ${cmakeBuildDirBin.absolutePath}")
    }
}
