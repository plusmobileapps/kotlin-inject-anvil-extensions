plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    androidTarget()

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
    namespace = "com.plusmobileapps.kotlin.inject.decompose.runtime"
    compileSdk = 35
}

dependencies {
    commonMainImplementation(libs.kotlin.inject.core.runtime)
    commonMainImplementation(libs.kotlin.inject.anvil.runtime)
    commonMainImplementation(libs.arkivanov.decompose.core)
}