plugins {
    alias(libs.plugins.julie.kotlinMultiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.feature.audiocapture.api)
            
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
        }
        
        desktopMain.dependencies {
            // Desktop-specific JNI implementations
        }
    }
}

// Task to build the native audio capture library using CMake
tasks.register<Exec>("buildNativeLibrary") {
    workingDir = file("src/main/cpp")
    
    doFirst {
        // Create build directory
        val buildDir = file("src/main/cpp/build")
        buildDir.mkdirs()
    }
    
    if (org.gradle.internal.os.OperatingSystem.current().isMacOsX) {
        commandLine("sh", "-c", """
            cd build && 
            cmake .. -DCMAKE_BUILD_TYPE=Release && 
            make -j4
        """.trimIndent())
    } else {
        throw GradleException("Audio capture is currently only supported on macOS")
    }
    
    description = "Build native audio capture library using CMake"
}

// Task to copy native libraries to resources directory
tasks.register("copyNativeLibsForDesktop") {
    dependsOn("buildNativeLibrary")
    doLast {
        val sourceFile = file("src/desktopMain/resources/libaudiocapture_jni.dylib")
        val targetDir = file("src/desktopMain/resources")
        
        if (sourceFile.exists()) {
            println("Native library already copied to: ${sourceFile.absolutePath}")
        } else {
            throw GradleException("Native library not found at: ${sourceFile.absolutePath}")
        }
    }
}

// Ensure native libs are built before desktop compilation
tasks.named("compileKotlinDesktop") {
    dependsOn("copyNativeLibsForDesktop")
}