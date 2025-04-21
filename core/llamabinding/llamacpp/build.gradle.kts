import java.nio.file.Files
import java.nio.file.Paths
import java.util.Properties

// Function to read properties file safely
fun loadProperties(file: File): Properties {
    val properties = Properties()
    if (file.exists()) {
        file.inputStream().use { properties.load(it) }
    }
    return properties
}

// Load local properties to find SDK/NDK paths
val localPropertiesFile = rootProject.file("local.properties")
val localProperties = loadProperties(localPropertiesFile)
val sdkDir = localProperties.getProperty("sdk.dir") ?: System.getenv("ANDROID_HOME")
// Assume NDK is located within sdkDir/ndk/<version>
val ndkVersion = "29.0.13113456" // Specify the required NDK version
val ndkDir = sdkDir?.let { File(it, "ndk/${ndkVersion}").absolutePath }

// Check if NDK path is valid
if (ndkDir == null || !File(ndkDir).isDirectory) {
    logger.warn("Android NDK directory not found at expected path '$sdkDir/ndk/$ndkVersion'. Ensure ANDROID_HOME is set correctly or sdk.dir is present in local.properties, and NDK version $ndkVersion exists. Android native build will be skipped.")
}
val ndkToolchainFile = ndkDir?.let { File(it, "build/cmake/android.toolchain.cmake").absolutePath }

// Apply the base plugin to get standard lifecycle tasks like 'build'
plugins { `base` }

// --- Configuration for Host (Desktop) Build --- (Android build is now handled by AGP externalNativeBuild)
val hostCmakeBuildDir = "${buildDir}/cmake-build-host"
val cmakeSrcDir = project.projectDir // CMakeLists.txt is at the root of llamacpp

tasks.register<Exec>("configureHostCMake") {
    group = "Build Native"
    description = "Configures the host build using CMake."
    workingDir = cmakeSrcDir
    outputs.dir(hostCmakeBuildDir)
    inputs.file(project.file("CMakeLists.txt"))
    inputs.dir(project.file("src/llama.cpp"))
    inputs.dir(project.file("src/main/cpp"))

    doFirst {
        Files.createDirectories(Paths.get(hostCmakeBuildDir))
    }
    commandLine(
        "/usr/bin/env", "cmake", ".",
        "-B", hostCmakeBuildDir,
        "-DCMAKE_BUILD_TYPE=Release",
        "-DLLAMA_BUILD_TESTS=OFF",
        "-DGGML_BUILD_TESTS=OFF",
        // Add host-specific flags if needed, e.g., METAL for macOS
        // "-DLLAMA_METAL=ON" // Example
    )
}

tasks.register<Exec>("buildHostCMake") {
    group = "Build Native"
    description = "Builds the host shared library using CMake."
    dependsOn("configureHostCMake")
    workingDir = project.file(hostCmakeBuildDir)
    outputs.dir("${hostCmakeBuildDir}/lib") // Adjust if output location differs
    outputs.dir("${hostCmakeBuildDir}/bin") // Adjust if output location differs

    commandLine("/usr/bin/env", "cmake", "--build", ".", "--config", "Release")
}

tasks.register<Copy>("copyHostNativeLib") {
    group = "Build Native"
    description = "Copies the host native library and its dependencies to composeApp resources."
    dependsOn("buildHostCMake")

    val libsToCopy = System.getProperty("os.name").lowercase().let {
        val jniLib = when {
            it.contains("mac") -> System.mapLibraryName("llama_jni") // libllama_jni.dylib
            it.contains("linux") -> System.mapLibraryName("llama_jni") // libllama_jni.so
            it.contains("win") -> "llama_jni.dll"
            else -> "libllama_jni.unknown"
        }
        listOf(
            jniLib,
            System.mapLibraryName("llama"),
            System.mapLibraryName("ggml"),
            // Add other host dependencies as needed based on CMake output
        ).filterNotNull()
    }

    logger.lifecycle("Host libraries to copy: $libsToCopy")

    // Assuming JNI lib is in 'lib' and dependencies are in 'bin' after build
    from("${hostCmakeBuildDir}/lib") { include(libsToCopy.first()) }
    from("${hostCmakeBuildDir}/bin") { include(libsToCopy.drop(1)) }

    into(project(":composeApp").layout.buildDirectory.dir("processedResources/desktop/main"))
}

// --- Configuration for Android NDK Build --- (REMOVED - Handled by AGP externalNativeBuild)
// val androidAbi = "arm64-v8a" // Target ABI
// val androidApiLevel = 24 // Minimum API level
// val androidCmakeBuildDir = "${buildDir}/cmake-build-android-${androidAbi}"
// tasks.register<Exec>("configureAndroidCMake_${androidAbi}") { ... }
// tasks.register<Exec>("buildAndroidCMake_${androidAbi}") { ... }
// tasks.register<Copy>("copyAndroidNativeLib_${androidAbi}") { ... }


// --- Aggregate Build Tasks ---

// Make the default 'build' task depend ONLY on the host copy task
// Android native build is triggered by AGP via externalNativeBuild
tasks.named("build") {
    dependsOn(tasks.named("copyHostNativeLib"))
    // Removed Android dependency
    // if (ndkToolchainFile != null) {
    //    dependsOn(tasks.named("copyAndroidNativeLib_${androidAbi}"))
    // }
}

// Clean task to remove CMake build directories
tasks.register<Delete>("cleanCMakeBuilds") {
    group = "Build"
    description = "Deletes all CMake build directories."
    delete(hostCmakeBuildDir) // Only clean host build dir now
    // delete(androidCmakeBuildDir) // Removed
}

tasks.named("clean") {
    dependsOn(tasks.named("cleanCMakeBuilds"))
}

// --- Old tasks ref (commented out previously) --- 
/* ... */
