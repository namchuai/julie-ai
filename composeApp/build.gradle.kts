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
            implementation(libs.filekit.dialogs)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)

            implementation(projects.core.model)
            implementation(projects.feature.thread.api)
            implementation(projects.feature.thread.impl)
            implementation(projects.feature.toolmanagement.api)
            implementation(projects.feature.toolmanagement.impl)
            implementation(projects.feature.modelconfig.impl)
            implementation(projects.feature.chat)
            implementation(projects.feature.modelmarket)
            implementation(projects.feature.modelmanagement.api)
            implementation(projects.feature.modelmanagement.impl)
            implementation(projects.feature.message.api)
            implementation(projects.feature.message.impl)
            implementation(projects.feature.jinjaparser.impl)
            implementation(projects.feature.promptlab)
            implementation(projects.feature.pythonrunner.api)
            implementation(projects.feature.pythonrunner.impl)
            implementation(projects.feature.hardwaremonitor.api)
            implementation(projects.feature.hardwaremonitor.impl)
            implementation(projects.feature.appsetting.api)
            implementation(projects.feature.appsetting.impl)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.composeVM)
            implementation(libs.navigation.compose)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        val desktopMain by getting
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.filekit.dialogs)
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
            isMinifyEnabled = false  // Temporarily disable ProGuard for desktop builds
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

        // Define paths to all possible native library locations
        val llamacppProject = project(":core:llamabinding:llamacpp")
        val cmakeBuildDirLib = llamacppProject.buildDir.resolve("cmake-build-host/lib")
        val cmakeBuildDirBin = llamacppProject.buildDir.resolve("cmake-build-host/bin")
        val processedResourcesDir =
            layout.buildDirectory.dir("processedResources/desktop/main").get().asFile

        // Build library path with all possible locations
        val libraryPaths = listOf(
            cmakeBuildDirLib.absolutePath,
            cmakeBuildDirBin.absolutePath,
            processedResourcesDir.absolutePath,
            System.getProperty("user.dir")
        )

        val libraryPath = libraryPaths.joinToString(File.pathSeparator)
        jvmArgs += "-Djava.library.path=$libraryPath"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ai.julie"
            packageVersion = "1.0.0"

            // Include the native library from the nativelib module's build output
            modules("java.instrument") // Example existing module, keep others if present
            includeAllModules = true // Ensure dependent modules are included
            
            linux {
                modules("jdk.security.auth")
            }
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
    dependsOn(project(":core:llamabinding:llamacpp").tasks.named("buildHostCMake"))
    dependsOn(project(":core:llamabinding:llamacpp").tasks.named("copyHostNativeLib"))

    // Set the system property directly on the task
    val llamacppProject = project(":core:llamabinding:llamacpp")
    val cmakeBuildDirLib = llamacppProject.buildDir.resolve("cmake-build-host/lib")
    val cmakeBuildDirBin = llamacppProject.buildDir.resolve("cmake-build-host/bin")

    // Also include the processed resources directory where libraries are copied
    val processedResourcesDir =
        layout.buildDirectory.dir("processedResources/desktop/main").get().asFile

    // Build library path with all possible locations
    val libraryPaths = mutableListOf<String>()

    // Add cmake build directories
    libraryPaths.add(cmakeBuildDirLib.absolutePath)
    libraryPaths.add(cmakeBuildDirBin.absolutePath)

    // Add processed resources directory
    libraryPaths.add(processedResourcesDir.absolutePath)

    // Add current working directory as fallback
    libraryPaths.add(System.getProperty("user.dir"))

    val libraryPath = libraryPaths.joinToString(File.pathSeparator)

    systemProperty("java.library.path", libraryPath)

    // Log the library path for debugging
    doFirst {
        println("java.library.path set to: $libraryPath")
        println("Looking for libllama_jni.dylib in:")
        libraryPaths.forEach { path ->
            val file = File(path, "libllama_jni.dylib")
            println("  $path -> ${if (file.exists()) "FOUND" else "NOT FOUND"}")
        }
    }
}
