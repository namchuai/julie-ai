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

// --- Configuration for Host (Desktop) Build ---
private val hostCmakeBuildDirName = "cmake-build-host"
private fun getHostCmakeBuildDir() = layout.buildDirectory.dir(hostCmakeBuildDirName).get().asFile

tasks.register<Exec>("configureHostCMake") {
    group = "Build Native"
    description = "Configures the host build using CMake."
    workingDir = layout.projectDirectory.asFile

    // Improved input tracking
    inputs.file(layout.projectDirectory.file("CMakeLists.txt"))
    inputs.dir(layout.projectDirectory.dir("src/whisper.cpp"))
    inputs.dir(layout.projectDirectory.dir("src/main/cpp"))
    inputs.property("buildType", "Release")

    // Output tracking
    outputs.dir(getHostCmakeBuildDir())
    outputs.cacheIf { true } // Enable caching

    // Only run if inputs changed or outputs don't exist
    onlyIf {
        !File(getHostCmakeBuildDir(), "CMakeCache.txt").exists() || inputs.hasInputs
    }

    doFirst {
        Files.createDirectories(getHostCmakeBuildDir().toPath())
    }
    commandLine(
        "/usr/bin/env", "cmake", ".",
        "-B", getHostCmakeBuildDir().absolutePath,
        "-DCMAKE_BUILD_TYPE=Release",
        "-DWHISPER_BUILD_TESTS=OFF",
        "-DWHISPER_BUILD_EXAMPLES=OFF"
    )
}

tasks.register<Exec>("buildHostCMake") {
    group = "Build Native"
    description = "Builds the host shared library using CMake."
    dependsOn("configureHostCMake")
    workingDir = getHostCmakeBuildDir()

    // Input tracking - the configuration and source files
    inputs.dir(getHostCmakeBuildDir()).withPropertyName("cmakeConfig")
    inputs.dir(layout.projectDirectory.dir("src/whisper.cpp")).withPropertyName("whisperSource")
    inputs.dir(layout.projectDirectory.dir("src/main/cpp")).withPropertyName("jniSource")
    inputs.property("buildConfig", "Release")

    // Output tracking
    outputs.dir(File(getHostCmakeBuildDir(), "lib")).withPropertyName("libDir")
    outputs.dir(File(getHostCmakeBuildDir(), "bin")).withPropertyName("binDir")
    outputs.cacheIf { true } // Enable caching

    // Only run if libraries don't exist or inputs changed
    val libsToCopy = System.getProperty("os.name").lowercase().let {
        val jniLib = when {
            it.contains("mac") -> System.mapLibraryName("whisper_jni") // libwhisper_jni.dylib
            it.contains("linux") -> System.mapLibraryName("whisper_jni") // libwhisper_jni.so  
            it.contains("win") -> "whisper_jni.dll"
            else -> "libwhisper_jni.unknown"
        }
        listOf(jniLib, System.mapLibraryName("whisper"), System.mapLibraryName("ggml"))
    }

    onlyIf {
        libsToCopy.any { lib ->
            !File(getHostCmakeBuildDir(), "lib/$lib").exists() && !File(getHostCmakeBuildDir(), "bin/$lib").exists()
        }
    }

    commandLine("/usr/bin/env", "cmake", "--build", ".", "--config", "Release")
}

tasks.register<Copy>("copyHostNativeLib") {
    group = "Build Native"
    description = "Copies the host native library and its dependencies to composeApp resources."
    dependsOn("buildHostCMake")

    val libsToCopy = System.getProperty("os.name").lowercase().let {
        val jniLib = when {
            it.contains("mac") -> System.mapLibraryName("whisper_jni") // libwhisper_jni.dylib
            it.contains("linux") -> System.mapLibraryName("whisper_jni") // libwhisper_jni.so
            it.contains("win") -> "whisper_jni.dll"
            else -> "libwhisper_jni.unknown"
        }
        listOf(
            jniLib,
            System.mapLibraryName("whisper"),
            System.mapLibraryName("ggml"),
            // Add other host dependencies as needed based on CMake output
        ).filterNotNull()
    }

    val targetDir =
        project(":composeApp").layout.buildDirectory.dir("processedResources/desktop/main")

    // Input tracking
    inputs.files(fileTree(File(getHostCmakeBuildDir(), "lib")) { include(libsToCopy) })
    inputs.files(fileTree(File(getHostCmakeBuildDir(), "bin")) { include(libsToCopy) })

    // Output tracking  
    outputs.dir(targetDir)
    outputs.cacheIf { true } // Enable caching

    // Only run if target files don't exist or source files are newer
    onlyIf {
        libsToCopy.any { lib ->
            val srcLib = File(getHostCmakeBuildDir(), "lib/$lib")
            val srcBin = File(getHostCmakeBuildDir(), "bin/$lib")
            val target = targetDir.get().asFile.resolve(lib)

            val srcFile = when {
                srcLib.exists() -> srcLib
                srcBin.exists() -> srcBin
                else -> null
            }

            srcFile != null && (!target.exists() || srcFile.lastModified() > target.lastModified())
        }
    }

    logger.lifecycle("Host libraries to copy: $libsToCopy")

    // Assuming JNI lib is in 'lib' and dependencies are in 'bin' after build
    from(File(getHostCmakeBuildDir(), "lib")) { include(libsToCopy.first()) }
    from(File(getHostCmakeBuildDir(), "bin")) { include(libsToCopy.drop(1)) }

    into(targetDir)
}

// Make the default 'build' task depend ONLY on the host copy task
// Android native build is triggered by AGP via externalNativeBuild
tasks.named("build") {
    dependsOn(tasks.named("copyHostNativeLib"))
}

// Clean task to remove CMake build directories
tasks.register<Delete>("cleanCMakeBuilds") {
    group = "Build"
    description = "Deletes all CMake build directories."
    delete(getHostCmakeBuildDir()) // Only clean host build dir now
}

tasks.named("clean") {
    dependsOn(tasks.named("cleanCMakeBuilds"))
}