package ai.julie

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
internal fun Project.configureKotlinMultiplatform(
    extension: KotlinMultiplatformExtension,
) = extension.apply {
    jvmToolchain(17)

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    )

    applyDefaultHierarchyTemplate()

    sourceSets.apply {
        commonMain {
            dependencies {
                implementation(libs.findLibrary("kotlinx.coroutines.core").get())
                implementation(libs.findLibrary("markdown.renderer").get())
                api(libs.findLibrary("koin.core").get())
            }
        }

        androidMain {
            dependencies {
                implementation(libs.findLibrary("koin.android").get())
                implementation(libs.findLibrary("kotlinx.coroutines.android").get())
            }
        }

        getByName("desktopMain") {
            dependencies {
                implementation(libs.findLibrary("kotlinx.coroutines.swing").get())
            }
        }

        // Apply opt-in universally to ensure consistency across the hierarchy
        all {
            languageSettings.optIn("kotlin.uuid.ExperimentalUuidApi")
            languageSettings.optIn("com.aallam.openai.api.BetaOpenAI")
        }
    }
}