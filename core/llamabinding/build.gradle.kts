import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.julie.kotlinMultiplatform)
}

kotlin {
    // Configure the androidTarget specifically for native build integration
    androidTarget {
        // Access the Android Library extension configured by the convention plugin
        // to add externalNativeBuild configuration JUST for this module.
        project.extensions.configure<LibraryExtension> {

            defaultConfig {
                // You might need ndkVersion specified if AGP doesn't find it automatically
                // ndkVersion = "29.0.13113456" // Example

                // Specify the ABIs to build. Remove armeabi-v7a.
                ndk {
                    abiFilters.clear()
                    // we don't support armeabi-v7a anymore.
                    abiFilters.addAll(
                        listOf(
                            "arm64-v8a",
                            "x86_64"
                        )
                    )
                }

                externalNativeBuild {
                    cmake {
                        // Pass arguments to CMake during configuration.
                        // AGP handles NDK toolchain, ABI, platform level.
                        arguments.addAll(
                            listOf(
                                "-DLLAMA_BUILD_TESTS=OFF",
                                "-DGGML_BUILD_TESTS=OFF"
                                // Add other necessary CMake defines here
                            )
                        )
                        // Optional: Specify specific targets if CMakeLists defines many
                        // targets.add("llama_jni") 
                    }
                }
            }

            // Configure the path to the CMakeLists.txt file for this project.
            // Path is relative to this build.gradle.kts file.
            externalNativeBuild {
                cmake {
                    // Version needs to be compatible with your CMakeLists.txt and NDK
                    // Find required version in NDK docs or CMake output if it fails.
                    // version = "3.22.1" // Example, adjust as needed
                    path = project.file("llamacpp/CMakeLists.txt")
                }
            }
        }
    }

    dependencies {
        sourceSets {
            commonMain.dependencies {
                implementation(projects.core.model)
            }
        }
    }
}

// Task to copy native libraries for desktop
tasks.register("copyNativeLibsForDesktop") {
    dependsOn(":core:llamabinding:llamacpp:buildHostCMake")
    doLast {
        val sourceDir = layout.projectDirectory.dir("llamacpp/build/cmake-build-host/lib").asFile
        val targetDir = layout.projectDirectory.dir("src/desktopMain/resources").asFile

        if (sourceDir.exists()) {
            targetDir.mkdirs()
            copy {
                from(sourceDir)
                into(targetDir)
                include("*.dylib", "*.so", "*.dll")
            }
            println("Copied native libraries from $sourceDir to $targetDir")
        } else {
            println("Source directory $sourceDir does not exist")
        }
    }
}

// Ensure native libs are copied before desktop compilation
tasks.named("compileKotlinDesktop") {
    dependsOn("copyNativeLibsForDesktop")
}
