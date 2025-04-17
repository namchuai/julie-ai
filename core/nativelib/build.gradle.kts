import java.nio.file.Files
import java.nio.file.Paths

// Apply the base plugin to get standard lifecycle tasks like 'build'
apply(plugin = "base")

// Define directories for CMake build
val cmakeBuildDir = "${buildDir}/cmake-build"

// Task to configure the CMake project
tasks.register<Exec>("configureCMake") {
    workingDir = project.projectDir
    // Create the build directory if it doesn't exist
    doFirst {
        Files.createDirectories(Paths.get(cmakeBuildDir))
    }
    // Command to run CMake configuration
    commandLine("cmake", ".", "-B", cmakeBuildDir, "-DCMAKE_BUILD_TYPE=Release")
    // Optional: Add generator, build type, etc.
    // commandLine("cmake", ".", "-B", cmakeBuildDir, "-G", "Ninja", "-DCMAKE_BUILD_TYPE=Debug")
}

// Task to build the CMake project
tasks.register<Exec>("buildCMake") {
    dependsOn("configureCMake")
    workingDir = project.file(cmakeBuildDir)
    // Command to run the CMake build process
    commandLine("cmake", "--build", ".")
}

// Task to copy the built library to the final destination
tasks.register<Copy>("copyNativeLib") {
    dependsOn("buildCMake")
    val libName = System.getProperty("os.name").lowercase().let {
        when {
            it.contains("mac") -> "libnative.dylib"
            it.contains("linux") -> "libnative.so"
            it.contains("win") -> "native.dll"
            else -> "libnative.unknown"
        }
    }
    // Copy from the new standard CMake output directory
    from("${cmakeBuildDir}/lib") { // Updated path
        include(libName)
    }
    // Copy to composeApp's processed resources for packaging
    into(project(":composeApp").layout.buildDirectory.dir("processedResources/desktop/main"))
}

// Make the default build task depend on the copy task
tasks.named("build") {
    dependsOn(tasks.named("copyNativeLib")) // Depend on the registered task
}

// Optional: Clean task to remove CMake build directory
tasks.register<Delete>("cleanCMake") { // Use a different name to avoid conflict with default clean
    delete(project.file(cmakeBuildDir))
}

tasks.named("clean") {
    dependsOn(tasks.named("cleanCMake")) // Make default clean depend on cleanCMake
}
