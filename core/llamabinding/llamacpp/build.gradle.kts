import java.nio.file.Files

plugins { base }

private val cmakeOutputDirName = "cmake-build-host"
private val nativeLibraries = listOf(
    "llama_jni",
    "llama",
    "ggml",
    "ggml-base",
    "ggml-cpu",
    "ggml-metal",
    "ggml-blas"
)

private fun getPlatformLibraries(): List<String> {
    val osName = System.getProperty("os.name")?.lowercase()
        ?: throw GradleException("Unable to detect operating system: os.name property is null")

    // Validate platform is supported with precise matching
    val isSupported = when {
        osName.startsWith("mac") -> true      // "mac os x"
        osName == "linux" -> true              // exactly "linux"
        osName.startsWith("windows") -> true   // "windows 10", "windows 11", etc.
        else -> false
    }

    if (!isSupported) {
        throw GradleException("Unsupported operating system: '$osName'. Supported: macOS, Linux, Windows")
    }

    // System.mapLibraryName handles all platform-specific naming conventions
    return nativeLibraries.map { System.mapLibraryName(it) }
}

private fun getBuildOutputDir() = layout.buildDirectory.dir(cmakeOutputDirName).get().asFile

tasks.register<Exec>("configureHostCMake") {
    group = "Build Native"
    description = "Configures the host build using CMake."
    workingDir = layout.projectDirectory.asFile

    // Improved input tracking  
    inputs.file(layout.projectDirectory.file("CMakeLists.txt"))
    inputs.dir(layout.projectDirectory.dir("src/llama.cpp"))
    inputs.dir(layout.projectDirectory.dir("src/main/cpp"))
    inputs.property("buildType", "Release")
    inputs.property("llamaTests", "OFF")
    inputs.property("ggmlTests", "OFF")

    // Output tracking
    outputs.dir(getBuildOutputDir())
    outputs.cacheIf { true } // Enable caching

    // Only run if inputs changed or outputs don't exist
    onlyIf {
        !File(getBuildOutputDir(), "CMakeCache.txt").exists() || inputs.hasInputs
    }

    doFirst {
        Files.createDirectories(getBuildOutputDir().toPath())
    }
    commandLine(
        "/usr/bin/env", "cmake", ".",
        "-B", getBuildOutputDir().absolutePath,
        "-DCMAKE_BUILD_TYPE=Release",
        "-DLLAMA_BUILD_TESTS=OFF",
        "-DGGML_BUILD_TESTS=OFF",
        "-DLLAMA_METAL=ON"
    )
}

tasks.register<Exec>("buildHostCMake") {
    group = "Build Native"
    description = "Builds the host shared library using CMake."
    dependsOn("configureHostCMake")
    workingDir = getBuildOutputDir()

    // Input tracking - the configuration and source files
    inputs.dir(getBuildOutputDir()).withPropertyName("cmakeConfig")
    inputs.dir(layout.projectDirectory.dir("src/llama.cpp")).withPropertyName("llamaSource")
    inputs.dir(layout.projectDirectory.dir("src/main/cpp")).withPropertyName("jniSource")
    inputs.property("buildConfig", "Release")

    // Output tracking
    outputs.dir(File(getBuildOutputDir(), "lib")).withPropertyName("libDir")
    outputs.dir(File(getBuildOutputDir(), "bin")).withPropertyName("binDir")
    outputs.cacheIf { true } // Enable caching

    // Only run if libraries don't exist or inputs changed
    val libsToCopy = getPlatformLibraries()

    onlyIf {
        libsToCopy.any { lib ->
            !File(getBuildOutputDir(), "lib/$lib").exists() && !File(getBuildOutputDir(), "bin/$lib").exists()
        }
    }

    commandLine("/usr/bin/env", "cmake", "--build", ".", "--config", "Release")
}

tasks.register<Copy>("copyHostNativeLib") {
    group = "Build Native"
    description =
        "Copies the host native library and its dependencies to composeApp resources for packaging."
    dependsOn("buildHostCMake")

    val libsToCopy = getPlatformLibraries()

    val targetDir =
        project(":composeApp").layout.buildDirectory.dir("processedResources/desktop/main")

    // Input tracking
    inputs.files(fileTree(File(getBuildOutputDir(), "lib")) { include(libsToCopy) })
    inputs.files(fileTree(File(getBuildOutputDir(), "bin")) { include(libsToCopy) })

    // Output tracking
    outputs.dir(targetDir)
    outputs.cacheIf { true } // Enable caching

    // Only run if target files don't exist or source files are newer
    onlyIf {
        libsToCopy.any { lib ->
            val srcLib = File(getBuildOutputDir(), "lib/$lib")
            val srcBin = File(getBuildOutputDir(), "bin/$lib")
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

    // Copy libraries from both lib and bin directories
    from(File(getBuildOutputDir(), "lib")) { include(libsToCopy) }
    from(File(getBuildOutputDir(), "bin")) { include(libsToCopy) }

    into(targetDir)
}

tasks.named<Delete>("clean") {
    delete(getBuildOutputDir())
}
