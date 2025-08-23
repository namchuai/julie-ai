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
                        arguments.addAll(
                            listOf(
                                "-DWHISPER_BUILD_TESTS=OFF",
                                "-DWHISPER_BUILD_EXAMPLES=OFF"
                            )
                        )
                    }
                }
            }

            // Configure the path to the CMakeLists.txt file for this project.
            externalNativeBuild {
                cmake {
                    path = project.file("whispercpp/CMakeLists.txt")
                }
            }
        }
    }

    dependencies {
        sourceSets {
            commonMain.dependencies {
                implementation(projects.core.model)
                implementation(libs.filekit.core)
            }
        }
    }
}

// Task to copy native libraries for desktop
tasks.register("copyNativeLibsForDesktop") {
    dependsOn(":core:whisperbinding:whispercpp:buildHostCMake")
    doLast {
        val sourceDir = file("whispercpp/build/lib")
        val resourceDir = file("src/desktopMain/resources")
        val buildDir = file("build/native-libs")

        if (sourceDir.exists()) {
            // Copy to resources (for JAR packaging)
            resourceDir.mkdirs()
            copy {
                from(sourceDir)
                into(resourceDir)
                include("*.dylib", "*.so", "*.dll")
            }
            println("Copied native libraries from $sourceDir to $resourceDir")

            // Copy to build directory (for direct loading)
            buildDir.mkdirs()
            copy {
                from(sourceDir)
                into(buildDir)
                include("*.dylib", "*.so", "*.dll")
            }

            // Also copy to composeApp directory (for when running from composeApp)
            val composeAppBuildDir = file("../../composeApp/core/whisperbinding/build/native-libs")
            composeAppBuildDir.mkdirs()
            copy {
                from(sourceDir)
                into(composeAppBuildDir)
                include("*.dylib", "*.so", "*.dll")
            }

            // Also copy the additional ggml libraries
            val ggmlSources = listOf(
                "whispercpp/build/src/whisper.cpp/ggml/src/ggml-blas",
                "whispercpp/build/src/whisper.cpp/ggml/src/ggml-metal",
                "whispercpp/build/src/whisper.cpp/ggml/src",
            )

            for (ggmlSource in ggmlSources) {
                val ggmlDir = file(ggmlSource)
                if (ggmlDir.exists()) {
                    copy {
                        from(ggmlDir)
                        into(resourceDir)
                        include("*.dylib", "*.so", "*.dll")
                    }
                    copy {
                        from(ggmlDir)
                        into(buildDir)
                        include("*.dylib", "*.so", "*.dll")
                    }
                    copy {
                        from(ggmlDir)
                        into(composeAppBuildDir)
                        include("*.dylib", "*.so", "*.dll")
                    }
                }
            }

            println("Copied all native libraries to $buildDir")
        } else {
            println("Source directory $sourceDir does not exist")
            println("Available directories in whispercpp/build/:")
            file("whispercpp/build").listFiles()?.forEach {
                println("  - ${it.name}")
            }
        }
    }
}

// Ensure native libs are copied before desktop compilation
tasks.named("compileKotlinDesktop") {
    dependsOn("copyNativeLibsForDesktop")
}