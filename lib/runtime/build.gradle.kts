import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    id("runtime.publication")
}

group = "com.plusmobileapps.kotlin.inject.anvil.extensions.runtime"
version = libs.versions.assistedFactory.get()

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "KotlinInjectAnvilDecompose"
            isStatic = true
        }
    }


    jvm("desktop")

    applyDefaultHierarchyTemplate()
}

android {
    namespace = "com.plusmobileapps.kotlin.inject.anvil.runtime"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    commonMainImplementation(libs.kotlin.inject.core.runtime)
    commonMainImplementation(libs.kotlin.inject.anvil.runtime)
    commonMainImplementation(libs.arkivanov.decompose.core)
}